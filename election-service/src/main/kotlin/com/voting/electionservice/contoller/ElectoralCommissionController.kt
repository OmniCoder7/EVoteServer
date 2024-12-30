package com.voting.electionservice.contoller

import com.voting.electionservice.dto.request.ElectoralCommissionRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/electoral-commission")
class ElectoralCommissionController {

    @PostMapping("")
    fun createElectoralCommission(@RequestBody electoralCommissionRequest: ElectoralCommissionRequest) {

    }

    @GetMapping("/{code}")
    fun getElectoralCommissionById(@PathVariable code: String) {

    }

    @GetMapping("/{code}/elections")
    fun getElectionsByElectoralCommission(@PathVariable code: String) {

    }

    @PutMapping("/{code}/status")
    fun updateElectoralCommissionStatus(@PathVariable code: String, @RequestParam status: String) {

    }
}