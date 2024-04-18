package org.mockInvestment.simulation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mockInvestment.member.domain.Member;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "member_simulation_date")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSimulationDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "simulationDate")
    private Member member;

    private LocalDate simulationDate;


    public MemberSimulationDate(Member member, LocalDate simulationDate) {
        this.member = member;
        this.simulationDate = simulationDate;
    }

    public void reset() {
        simulationDate = LocalDate.of(2022, 2, 16);
    }

    public void updateDate(LocalDate newDate) {
        simulationDate = newDate;
    }

}
