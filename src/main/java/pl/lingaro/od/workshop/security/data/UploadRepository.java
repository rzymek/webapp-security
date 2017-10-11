package pl.lingaro.od.workshop.security.data;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UploadRepository extends JpaRepository<Upload, Integer> {
    List<FileInfo> findAll(Specification<Upload> specification);

    @Query("select file from Upload file where file.published=true order by file.timestamp desc")
    List<Upload> findPublished(Pageable pageable);

    default List<Upload> findPublished() {
       return findPublished(new PageRequest(0,100));
    }

    // alternative:

    List<Upload> findTop100ByPublishedTrueOrderByTimestamp();
}
