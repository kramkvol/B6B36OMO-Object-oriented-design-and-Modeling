package cz.cvut.fel.omo.hw.functions.statistics;

import cz.cvut.fel.omo.hw.functions.data.model.AbroadResults;
import cz.cvut.fel.omo.hw.functions.data.model.Candidates;
import cz.cvut.fel.omo.hw.functions.data.model.Country;
import cz.cvut.fel.omo.hw.functions.data.model.Continent;
import cz.cvut.fel.omo.hw.functions.utils.CandidateUtils;
import cz.cvut.fel.omo.hw.functions.utils.CandidateUtilsImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AbroadStatisticsImpl implements AbroadStatistics {

    private final CompletableFuture<AbroadResults> abroadResults;
    private final CandidateUtils candidateUtils;

    public AbroadStatisticsImpl(CompletableFuture<AbroadResults> abroadResults, CompletableFuture<Candidates> candidates) {
        this.abroadResults = abroadResults;
        this.candidateUtils = new CandidateUtilsImpl(candidates);
    }

    private List<Country> getAllCountries() {
        return abroadResults.join().getAbroad().getContinents().stream()
                .flatMap(continent -> continent.getCountries().stream())
                .collect(Collectors.toList());
    }

    private List<Continent> getAllContinents() {
        return abroadResults.join().getAbroad().getContinents();
    }

    @Override
    public String getNameOfCountryWithTheHighestNonValidVotesRatio() {
        return getAllCountries().stream()
                .max(Comparator.comparingDouble
                        (country ->
                                Optional.ofNullable(country.getVoterTurnout())
                                        .map(turnout -> turnout.getNumberOfSubmittedVotingEnvelopes() > 0
                                                ? ((double) (turnout.getNumberOfSubmittedVotingEnvelopes() - turnout.getNumberOfValidVotes()))
                                                / turnout.getNumberOfSubmittedVotingEnvelopes()
                                                : 0.0)
                                        .orElse(0.0)
                        ))
                .map(Country::getName)
                .orElse("Fail");
    }

    @Override
    public Map<String, List<String>> getCandidateVictoryCountryMap() {
        return getAllCountries().stream()
                .flatMap(country -> {
                    Optional<Integer> maxVotes = country.getVotes().stream()
                            .map(vote -> vote.getVotes())
                            .max(Comparator.naturalOrder());

                    List<String> winners = maxVotes.map(max ->
                            country.getVotes().stream()
                                    .filter(vote -> vote.getVotes() == max)
                                    .flatMap(vote -> candidateUtils.getCandidate(vote.getCandidateId())
                                            .stream().map(candidate -> candidate.getFullName()))
                                    .collect(Collectors.toList())
                    ).orElse(List.of());

                    return winners.stream().map(winner -> Map.entry(winner, country.getName()));
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    @Override
    public Map<String, Integer> getContinentRegisteredVoterCountMap() {
        return getAllContinents().stream()
                .collect(Collectors.toMap(
                        Continent::getName,
                        continent -> continent.getCountries().stream()
                                .mapToInt(country -> Optional.ofNullable(country.getVoterTurnout())
                                        .map(turnout -> turnout.getNumberOfRegisteredVoters())
                                        .orElse(0))
                                .sum()
                ));
    }

    @Override
    public String getNameOfCountryWithMostRegisteredVoters() {
        return getAllCountries().stream()
                .max(Comparator.comparingInt(country -> Optional.ofNullable(country.getVoterTurnout())
                        .map(turnout -> turnout.getNumberOfRegisteredVoters())
                        .orElse(0)))
                .map(Country::getName)
                .orElse("Fail");
    }
}
