package kr.hs.dgsw.web_326.Controller;

import kr.hs.dgsw.web_326.Protocol.AttachmentProtocol;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
public class AttachmentController {
    @PostMapping("/attachment")
    public AttachmentProtocol upload(@RequestPart MultipartFile srcFile) {
        String destFilename = "E:/Source/IdeaProjects/web_326/upload/"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"))
                + UUID.randomUUID().toString() + "_" + srcFile.getOriginalFilename();
        try {
            File destFile = new File(destFilename);
            destFile.getParentFile().mkdirs();
            srcFile.transferTo(destFile);
            return new AttachmentProtocol(destFilename, srcFile.getOriginalFilename());
        } catch (Exception e) {
            return null;
        }
    }
}
