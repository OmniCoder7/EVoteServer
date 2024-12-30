package com.voting.electionservice.contoller

import com.voting.electionservice.dto.request.PartyRequest
import com.voting.electionservice.model.type.PartyStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/party")
class PartyController {

    @PostMapping
    fun createParty(@RequestBody partyRequest: PartyRequest) {

    }

    @GetMapping("/{partyName}")
    fun getPartyByName(@PathVariable partyName: String) {

    }

    @GetMapping("/{partyName}/candidates")
    fun getPartyCandidates(@PathVariable partyName: String) {

    }

    @PutMapping("/{partyName}/status")
    fun putPartyStatus(@PathVariable partyName: String, @RequestBody status: PartyStatus) {

    }
}