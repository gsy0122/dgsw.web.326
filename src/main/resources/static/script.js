function openLoginDialog() {
    $('#container-1').show(500);
}
function closeLoginDialog() {
    $('#container-1').hide(500);
}
function openCommentDialog() {
    if (user == null) {
        openLoginDialog();
        return;
    }
    $('#container-2').show(500);
}
function closeCommentDialog() {
    $('#container-2').hide(500);
}
let storedPath = null;
let originalName = null;
async function addComment() {
    try {
        await uploadNewFile();
        let comment = {
            userId: user.id,
            content: $('#content').val(),
            originalName: originalName,
            storedPath: storedPath
        };
        console.log(JSON.stringify(comment));
        let response = await $.ajax({
            type: 'post',
            url: '/comment/add',
            contentType: 'application/json',
            data: JSON.stringify(comment)
        });
        originalName = null;
        storedPath = null;
        $('#content').val('');
        console.log(JSON.stringify(response));
        addCommentLine(response);
        closeCommentDialog();
    } catch (error) {
        console.log(JSON.stringify(error));
    }
}
async function uploadNewFile() {
    try {
        let file = $(`#upload-file`)[0].files[0];
        let formData = new FormData();
        formData.append('srcFile', file);
        let response = await $.ajax({
            type: 'post',
            url: '/attachment',
            data: formData,
            processData: false,
            contentType: false,
            success: (data) => {
                storedPath = data.storedPath;
                originalName = data.originalName;
            },
        });
    } catch (error) {
        console.log(JSON.stringify(error));
    }
}
async function uploadEditFile(id) {
    try {
        let file = $(`#upload-file${id}`)[0].files[0];
        if (file == null) {
            let comment = $.get(`/comment/view/${id}`);
            storedPath = comment.storedPath;
            originalName = comment.originalName;
            return;
        }
        let formData = new FormData();
        formData.append('srcFile', file);
        let response = await $.ajax({
            type: 'post',
            url: '/attachment',
            data: formData,
            processData: false,
            contentType: false,
            success: (data) => {
                storedPath = data.storedPath;
                originalName = data.originalName;
            },
        });
    } catch (error) {
        console.log(JSON.stringify(error));
    }
}
async function getCommentList() {
    try {
        let response = await $.get('/comment/list');
        for (let i = 0; i < response.length; i++) {
            addCommentLine(response[i]);
        }
    } catch (error) {
        $('#comment-list').html(JSON.stringify(error));
    }
}
function addCommentLine(response) {
    $('#comment-list').prepend(`
            <div id="line${response.id}" class="comment-line">
                <div style="width: 150px;">${response.username}</div>
                <div style="width: 350px;">${response.content}</div>
                <div></div>
                <img src="/attachment/comment/${response.id}" />
                <div>
                    <button onclick="editComment(this, ${response.id})">수정</button>
                    <button onclick="removeComment(this, ${response.id})">삭제</button>
                </div>
            </div>
        `);
}
let content = null;
async function editComment(button, id) {
    if (user == null) {
        openLoginDialog();
        return;
    }
    let line = $(`#line${id}`);
    if ($(button).text() == '수정') {
        content = line.find('div:nth-child(2)').html();
        let inputContent = `<input value="${content}">`;
        line.find('div:nth-child(2)').html(inputContent);
        let inputFile = `<input type="file" id="upload-file${id}">`;
        line.find('div:nth-child(3)').html(inputFile);
        line.find('img').hide();
        $(button).text('확인');
        $(button).next().text('취소');
    } else {
        await uploadEditFile(id);
        let comment = {
            userId: 1,
            content: line.find('input').val(),
            storedPath: storedPath,
            originalName: originalName
        };
        let response = await $.ajax({
            type: 'put',
            url: `/comment/update/${id}`,
            contentType: 'application/json',
            data: JSON.stringify(comment)
        });
        console.log(JSON.stringify(response));
        originalName = null;
        storedPath = null;
        line.find('div:nth-child(2)').html(comment.content);
        line.find('div:nth-child(3)').html('');
        line.find('img')[0].src = `/attachment/comment/${id}?${new Date()}`;
        line.find('img').show();
        $(button).text('수정');
        $(button).next().text('삭제');
    }
}
async function removeComment(button, id) {
    if (user == null) {
        openLoginDialog();
        return;
    }
    if ($(button).text() == '삭제') {
        try {
            let response = await $.ajax({
                type: 'delete',
                url: `/comment/remove/${id}`,
                contentType: 'application/json'
            });
            if (response) {
                $(`#line${id}`).remove();
            } else {
                alert('삭제에 실패하였습니다.');
            }
        } catch (error) {
            console.log(JSON.stringify(error));
        }
    } else {
        $(button).text('삭제');
        $(button).prev().text('수정');
        $(`#line${id}`).find('img').show();
        $(`#line${id}`).find('div:nth-child(2)').html(content);
        $(`#line${id}`).find('div:nth-child(3)').html('');
    }
}
async function login() {
    let login = {
        email: $('#email').val(),
        password: $('#password').val()
    };
    let response = await $.ajax({
        type: 'post',
        url: '/user/login',
        contentType: 'application/json',
        data: JSON.stringify(login),
        success: (data) => {
            closeLoginDialog();
            getUser(data.id);
        },
        error: (error) => {
            alert('로그인에 실패하였습니다.');
            console.log(JSON.stringify(error));
        },
    });
}
let user = null;
async function getUser(id) {
    $('#login-button').hide();
    user = await $.get(`/user/view/${id}`);
    $('#user').html(`
            <div><img src="attachment/user/${id}" id="profile-image" /></div>
            <div>${user.username}</div><br>
            <div style="width: 350px;">${user.email}</div><br>
            <div style="width: 350px;">${user.joined}</div>
        `);
}