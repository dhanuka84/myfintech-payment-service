package org.myfintech.payment.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import org.hibernate.Hibernate

@MappedSuperclass
open class AbstractEntity<T : Any>(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityColumnConstants.COMMON_ID, updatable = false, nullable = false)
    open var id: T? = null,

    @Column(name = EntityColumnConstants.COMMON_CREATED_DATE, nullable = false, updatable = false)
    open var createdDate: OffsetDateTime? = null,

    @Column(name = EntityColumnConstants.COMMON_MODIFIED_DATE, nullable = false)
    open var modifiedDate: OffsetDateTime? = null
) {
    @PrePersist
    protected fun onCreate() {
        val now = OffsetDateTime.now()
        createdDate = now
        modifiedDate = now
    }

    @PreUpdate
    protected fun onUpdate() {
        modifiedDate = OffsetDateTime.now()
    }

    // === equals/hashCode equivalent to Lombok @EqualsAndHashCode(onlyExplicitlyIncluded = true) on id ===
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        // proxy-safe type check
        if (Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AbstractEntity<*>
        // only id participates in equality
        return id != null && id == other.id
    }

    override fun hashCode(): Int = Hibernate.getClass(this).hashCode()
}
