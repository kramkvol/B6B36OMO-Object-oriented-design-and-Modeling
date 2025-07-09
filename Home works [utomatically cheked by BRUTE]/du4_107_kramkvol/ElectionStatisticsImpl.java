package cz.cvut.fel.omo.hw.functions.statistics;

import cz.cvut.fel.omo.hw.functions.data.model.AbroadResults;
import cz.cvut.fel.omo.hw.functions.data.model.Candidates;
import cz.cvut.fel.omo.hw.functions.data.model.RegionResults;
import cz.cvut.fel.omo.hw.functions.data.model.Vote;
import cz.cvut.fel.omo.hw.functions.data.model.VoterTurnout;
import cz.cvut.fel.omo.hw.functions.utils.CandidateUtils;
import cz.cvut.fel.omo.hw.functions.utils.CandidateUtilsImpl;
import cz.cvut.fel.omo.hw.functions.utils.ElectionsUtils;
import cz.cvut.fel.omo.hw.functions.utils.ElectionsUtilsImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ElectionStatisticsImpl implements ElectionStatistics {

    private final ElectionsUtils electionsUtils;
    private final CandidateUtils candidateUtils;

    public ElectionStatisticsImpl(CompletableFuture<List<RegionResults>> regionResultsFuture, CompletableFuture<AbroadResults> abroadResultsFuture, CompletableFuture<Candidates> candidatesFuture) {
        this.candidateUtils = new CandidateUtilsImpl(candidatesFuture);
        this.electionsUtils = new ElectionsUtilsImpl(regionResultsFuture, abroadResultsFuture);
    }

    @Override
    public int getTotalValidVotes() {
        return electionsUtils.getAllVotes().stream()
                .mapToInt(Vote::getVotes)
                .sum();
    }


    @Override
    public int getTotalInvalidVotes() {
        return electionsUtils.getAllVoterTurnouts().stream()
                .mapToInt(turnout -> turnout.getNumberOfSubmittedVotingEnvelopes() - turnout.getNumberOfValidVotes())
                .sum();
    }

    @Override
    public int getTotalVoterCount() {
        return electionsUtils.getAllVoterTurnouts().stream()
                .mapToInt(VoterTurnout::getNumberOfRegisteredVoters)
                .sum();
    }

    @Override
    public int getTotalIssuedEnvelopes() {
        return electionsUtils.getAllVoterTurnouts().stream()
                .mapToInt(VoterTurnout::getNumberOfIssuedVotingEnvelopes)
                .sum();
    }

    @Override
    public double getTotalVoterTurnout() {
        return getTotalVoterCount() == 0
                ? 0.0
                : ((double) getTotalIssuedEnvelopes() / getTotalVoterCount()) * 100;
    }


    @Override
    public Map<String, Integer> getCandidateVotesMap() {
        return electionsUtils.getAllVotes().stream()
                .collect(Collectors.groupingBy(
                        Vote::getCandidateId,
                        Collectors.summingInt(Vote::getVotes)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> candidateUtils.getCandidateFullName(entry.getKey()).orElse("Fail"),
                        Map.Entry::getValue
                ));
    }

    @Override
    public Map<String, Double> getCandidateVotesPercentMap() {
        return getCandidateVotesMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> getTotalValidVotes() == 0
                                ? 0.0
                                : (double) entry.getValue() / getTotalValidVotes() * 100
                ));
    }

    @Override
    public String getCandidatesByVotesDesc() {
        return getCandidateVotesMap().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> String.format("%s (%.2f%%)",
                        entry.getKey(),
                        ((double) entry.getValue() / getTotalValidVotes()) * 100))
                .collect(Collectors.joining(", "));
    }
}
