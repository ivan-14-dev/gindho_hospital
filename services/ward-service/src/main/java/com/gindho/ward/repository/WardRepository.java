package com.gindho.ward.repository;
import com.gindho.ward.model.Ward; import org.springframework.data.jpa.repository.JpaRepository;
public interface WardRepository extends JpaRepository<Ward, Long> {}