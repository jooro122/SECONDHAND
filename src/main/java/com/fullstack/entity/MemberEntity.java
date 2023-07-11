package com.fullstack.entity;

import lombok.*;

import org.hibernate.annotations.Cascade;
import org.springframework.data.annotation.CreatedDate;

import com.fullstack.dto.MemberDTO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "member_table")

/**
 * 이 클래스는 회원 정보를 데이터베이스에 저장하는 데 사용되는 엔티티(Entity)입니다.
 * 이 클래스에는 회원 정보와 관련된 필드와 해당 필드에 대한 제약 조건, 회원 가입 날짜를 저장하는 필드가 포함되어 있습니다.
 */
public class MemberEntity {
    @Id
    @Column(nullable = false, length = 30)
    private String memberId;

    @Column(nullable = false, length = 30)
    private String memberPassword;

    @Column(nullable = false, length = 30)
    private String memberName;
    
    @Column(nullable = false, length = 30)
    private String memberEmail;

    @Column(nullable = false, length = 30)
    private String memberPhoneNumber;

    @Column(name = "memberRegDate", updatable = false)
    private LocalDateTime memberRegDate;
    
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Board> boards = new ArrayList<>();
    
    //찜하기 기능 DB테이블
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "like_board",
               joinColumns = @JoinColumn(name = "member_id"),
               inverseJoinColumns = @JoinColumn(name = "board_mno"))
    private Set<Board> likedBoards = new HashSet<>();
    
    
    @ManyToMany(mappedBy = "likedBoards", cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<MemberEntity> likedMembers = new HashSet<>();
    
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Note> sentNotes = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Note> receivedNotes = new ArrayList<>();

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    

    // ...

    /**
     * 이 메소드는 엔티티가 데이터베이스에 저장되기 전에 회원의 가입 날짜를 설정합니다.
     * / @PrePersist 어노테이션은 해당 메소드를 엔티티가 영속화되기 전에 실행하도록 합니다.
     */
    @PrePersist
    public void memberRegDate() {
        // 현재 시간을 회원 가입 날짜로 설정합니다.
        this.memberRegDate = LocalDateTime.now();
    }

    public static MemberEntity toMemberEntity(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();

        memberEntity.setMemberId(memberDTO.getMemberId());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberPhoneNumber(memberDTO.getMemberPhoneNumber());
        memberEntity.setMemberRegDate(memberDTO.getMemberRegDate());
        memberEntity.setRole(Role.MEMBER);

        return memberEntity;
    }


    /**
     * 이 함수는 MemberDTO 객체를 입력으로 받아 MemberEntity 객체로 변환하는 기능을 수행합니다.
     */
    public static MemberEntity toUpdateMemberEntity(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();

        memberEntity.setMemberId(memberDTO.getMemberId());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberPhoneNumber(memberDTO.getMemberPhoneNumber());
        memberEntity.setRole(Role.MEMBER);
        return memberEntity;
    }


}
