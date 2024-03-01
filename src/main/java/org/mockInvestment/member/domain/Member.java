package org.mockInvestment.member.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mockInvestment.trade.domain.StockOrder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String role;

    private String username;

    @OneToMany(mappedBy = "member")
    private List<StockOrder> stockOrders;


    @Builder
    public Member(Long id, String name, String email, String role, String username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.username = username;
    }
}
