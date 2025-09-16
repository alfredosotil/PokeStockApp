package com.poke.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserProfile.
 */
@Entity
@Table(name = "user_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 1000)
    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "location")
    private String location;

    @Column(name = "favorite_set")
    private String favoriteSet;

    @Column(name = "playstyle")
    private String playstyle;

    @Lob
    @Column(name = "avatar")
    private byte[] avatar;

    @Column(name = "avatar_content_type")
    private String avatarContentType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBio() {
        return this.bio;
    }

    public UserProfile bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return this.location;
    }

    public UserProfile location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFavoriteSet() {
        return this.favoriteSet;
    }

    public UserProfile favoriteSet(String favoriteSet) {
        this.setFavoriteSet(favoriteSet);
        return this;
    }

    public void setFavoriteSet(String favoriteSet) {
        this.favoriteSet = favoriteSet;
    }

    public String getPlaystyle() {
        return this.playstyle;
    }

    public UserProfile playstyle(String playstyle) {
        this.setPlaystyle(playstyle);
        return this;
    }

    public void setPlaystyle(String playstyle) {
        this.playstyle = playstyle;
    }

    public byte[] getAvatar() {
        return this.avatar;
    }

    public UserProfile avatar(byte[] avatar) {
        this.setAvatar(avatar);
        return this;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getAvatarContentType() {
        return this.avatarContentType;
    }

    public UserProfile avatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
        return this;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserProfile user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((UserProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", bio='" + getBio() + "'" +
            ", location='" + getLocation() + "'" +
            ", favoriteSet='" + getFavoriteSet() + "'" +
            ", playstyle='" + getPlaystyle() + "'" +
            ", avatar='" + getAvatar() + "'" +
            ", avatarContentType='" + getAvatarContentType() + "'" +
            "}";
    }
}
