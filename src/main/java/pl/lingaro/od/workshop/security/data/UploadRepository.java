package pl.lingaro.od.workshop.security.data;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface UploadRepository extends Repository<Upload, Integer> {
    List<FileInfo> findAll(Specification<Upload> specification);
}
