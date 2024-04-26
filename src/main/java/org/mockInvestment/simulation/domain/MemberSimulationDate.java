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

    public static final LocalDate DEFAULT_START_DATE = LocalDate.of(2022, 2, 16);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "simulationDate")
    private Member member;

    private LocalDate simulationDate;


    public MemberSimulationDate(Member member) {
        this.member = member;
        this.simulationDate = DEFAULT_START_DATE;
    }

    public void reset() {
        simulationDate = DEFAULT_START_DATE;
    }

    public void updateDate(LocalDate newDate) {
        simulationDate = newDate;
    }

}
