package org.mockInvestment.simulation.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.global.auth.Login;
import org.mockInvestment.simulation.application.SimulationDateFindService;
import org.mockInvestment.simulation.application.SimulationProceedService;
import org.mockInvestment.simulation.application.SimulationStartService;
import org.mockInvestment.simulation.dto.SimulationDateResponse;
import org.mockInvestment.simulation.dto.request.ProceedSimulationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/simulation")
public class SimulationApi {

    private final SimulationDateFindService simulationDateFindService;

    private final SimulationStartService simulationStartService;

    private final SimulationProceedService simulationProceedService;


    @GetMapping("/now")
    public ResponseEntity<SimulationDateResponse> getCurrentDate(@Login AuthInfo authInfo) {
        SimulationDateResponse response = simulationDateFindService.findCurrentDate(authInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/next")
    public ResponseEntity<SimulationDateResponse> proceedNextDate(@RequestBody ProceedSimulationRequest request,
                                                                  @Login AuthInfo authInfo) {
        SimulationDateResponse response = simulationProceedService.proceedNextDate(request, authInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restart")
    public ResponseEntity<Void> restartSimulation(@Login AuthInfo authInfo) {
        simulationStartService.restartSimulation(authInfo);
        return ResponseEntity.noContent().build();
    }

}
