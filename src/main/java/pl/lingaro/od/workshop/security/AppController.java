package pl.lingaro.od.workshop.security;

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
import pl.lingaro.od.workshop.security.data.Upload;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class AppController {
    private static final Logger LOG = Logger.getLogger(AppController.class.getName());
    private final EntityManager em;

    public AppController(EntityManager em) {
        this.em = em;
    }

    @GetMapping("/")
    public String getPublishedFiles(Model model) {
        final List<Upload> files = em.createQuery("select file from Upload file " +
                " where file.published=true " +
                " order by file.timestamp desc", Upload.class)
                .setMaxResults(100)
                .getResultList();
        model.addAttribute("files", files);
        return "index";
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        Upload upload = em.find(Upload.class, id);
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
        String query = "select " +
                "   id,filename,timestamp,published, null as contents, null as description, null as owner  " +
                " from Upload " +
                " where " +
                "   owner = '" + principal.getName() + "'";
        if (name != null) {
            query += "  AND lower(filename) like '%" + name + "%'";
        }
        LOG.info(query);
        List<Upload> files = em.createNativeQuery(query, Upload.class).getResultList();
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
        em.persist(upload);
        return "redirect:/myfiles";
    }

    @RolesAllowed("ROLE_USER")
    @GetMapping("/publish/{id}")
    public String publish(@PathVariable("id") int id, Model model) throws IOException {
        final Upload upload = em.find(Upload.class, id);
        model.addAttribute("file", upload);
        return "publish";
    }

    @Transactional
    @PostMapping("/publish/{id}")
    public String publish(@PathVariable("id") int id, @RequestParam("description") String description) throws IOException {
        final Upload upload = em.find(Upload.class, id);
        upload.setPublished(true);
        upload.setDescription(description);
        return "redirect:/myfiles";
    }

    @RolesAllowed("ROLE_USER")
    @Transactional
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) throws IOException {
        em.remove(em.getReference(Upload.class, id));
        return "redirect:/myfiles";
    }
}
