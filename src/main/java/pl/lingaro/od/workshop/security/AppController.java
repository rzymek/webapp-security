package pl.lingaro.od.workshop.security;

import org.h2.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.lingaro.od.workshop.security.data.FileInfo;
import pl.lingaro.od.workshop.security.data.Upload;
import pl.lingaro.od.workshop.security.data.UploadRepository;

import javax.annotation.security.RolesAllowed;
import javax.persistence.criteria.Predicate;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class AppController {
    private static final Logger LOG = Logger.getLogger(AppController.class.getName());
    private final UploadRepository uploadRepository;

    public AppController(UploadRepository userRepository) {
        this.uploadRepository = userRepository;
    }

    @GetMapping("/")
    public String getPublishedFiles(Model model) {
        final List<Upload> files = uploadRepository.findPublished();
        model.addAttribute("files", files);
        return "index";
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        Upload upload = uploadRepository.findOne(id);
        if (upload == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        byte[] file = upload.getContents();
        response.setContentLengthLong(file.length);
        response.addHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-Disposition", "inline; filename=" + upload.getFilename());
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            FileCopyUtils.copy(file, outputStream);
        }
    }

    @RolesAllowed("ROLE_USER")
    @GetMapping("/myfiles")
    public String files(@RequestParam(value = "name", required = false) String name, Model model, Principal principal) {
        List<FileInfo> files = uploadRepository.findAll((root, criteriaQuery, criteria) -> {
            Predicate ownership = criteria.equal(
                    root.get("owner"), principal.getName()
            );
            if (StringUtils.isNullOrEmpty(name)) {
                return ownership;
            }
            return criteria.and(
                    ownership,
                    criteria.like(
                            criteria.lower(root.get("filename")),
                            '%' + name.toLowerCase() + '%'
                    )
            );
        });

        model.addAttribute("files", files);
        model.addAttribute("user", principal.getName());
        return "myfiles";
    }

    @RolesAllowed("ROLE_USER")
    @PostMapping("/upload")
    @Transactional
    public String handleUpload(@RequestParam("file") MultipartFile file, Principal principal) throws Exception {
        Upload upload = new Upload();
        upload.setFilename(file.getOriginalFilename());
        upload.setContents(StreamUtils.copyToByteArray(file.getInputStream()));
        upload.setOwner(principal.getName());
        uploadRepository.save(upload);
        return "redirect:/myfiles";
    }

    @RolesAllowed("ROLE_USER")
    @GetMapping("/publish/{id}")
    public String publish(@PathVariable("id") int id, Model model) throws IOException {
        final Upload upload = uploadRepository.findOne(id);
        model.addAttribute("file", upload);
        return "publish";
    }

    @Transactional
    @PostMapping("/publish/{id}")
    public String publish(@PathVariable("id") int id, @RequestParam("description") String description) throws IOException {
        final Upload upload = uploadRepository.findOne(id);
        upload.setPublished(true);
        upload.setDescription(description);
        return "redirect:/myfiles";
    }

    @RolesAllowed("ROLE_USER")
    @Transactional
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) throws IOException {
        uploadRepository.delete(id);
        return "redirect:/myfiles";
    }
}
