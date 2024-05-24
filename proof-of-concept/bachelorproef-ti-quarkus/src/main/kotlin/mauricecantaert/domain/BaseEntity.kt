package mauricecantaert.domain

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity : PanacheEntity() {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private var createdAt: LocalDateTime = LocalDateTime.now()

    @UpdateTimestamp
    @Column(name = "updated_at")
    private var updatedAt: LocalDateTime? = null
}