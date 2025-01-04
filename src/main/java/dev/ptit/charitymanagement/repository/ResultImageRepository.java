package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.ResultImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface ResultImageRepository extends JpaRepository<ResultImageEntity, String> {
}
