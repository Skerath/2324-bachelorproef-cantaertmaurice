package mauricecantaert.domain

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity(name = "trainer_client")
class User : BaseEntity() {

    @Column(name = "client_name")
    lateinit var userName: String

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "client")
    val requests: MutableList<EquipmentRequest> = mutableListOf()

    companion object : PanacheCompanion<User> {
        fun byName(name: String) = find("userName", name).firstResult()
    }

}
