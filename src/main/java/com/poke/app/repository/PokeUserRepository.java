package com.poke.app.repository;

import com.poke.app.domain.PokeUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PokeUser entity.
 */
@Repository
public interface PokeUserRepository extends JpaRepository<PokeUser, Long> {
    default Optional<PokeUser> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PokeUser> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PokeUser> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select pokeUser from PokeUser pokeUser left join fetch pokeUser.user",
        countQuery = "select count(pokeUser) from PokeUser pokeUser"
    )
    Page<PokeUser> findAllWithToOneRelationships(Pageable pageable);

    @Query("select pokeUser from PokeUser pokeUser left join fetch pokeUser.user")
    List<PokeUser> findAllWithToOneRelationships();

    @Query("select pokeUser from PokeUser pokeUser left join fetch pokeUser.user where pokeUser.id =:id")
    Optional<PokeUser> findOneWithToOneRelationships(@Param("id") Long id);
}
