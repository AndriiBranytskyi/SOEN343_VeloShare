package com.veloshare.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veloshare.application.usecases.StationService;

@RestController //marks a web controller whose return values go straight to the response body.
@RequestMapping("/api/stations") //base path for all methods.
public class StationController {

    private final StationService stations;

    public StationController(StationService stations) {
        this.stations = stations;
    }

        //for dashboard
    @GetMapping
        public ResponseEntity<?> list() {
            var all = stations.getAll();    
            return ResponseEntity.ok(all);
        }


    @GetMapping("/{name}") //handles GET for /api/stations/{name}.
    public ResponseEntity<?> get(@PathVariable String name) { //binds the URL segment {name} to the method parameter.
        //var figures out the type from the expression on RHS so here its instead of Result<Station>
        var r = stations.get(name);
        return r.isOk() ? ResponseEntity.ok(r.getValue())
                : ResponseEntity.badRequest().body(r.getError());
    }
}
