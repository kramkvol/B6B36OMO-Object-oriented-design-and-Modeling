package cz.cvut.fel.omo.hw.functions.statistics;

import cz.cvut.fel.omo.hw.functions.data.model.Candidates;
import cz.cvut.fel.omo.hw.functions.data.model.RegionResults;
import cz.cvut.fel.omo.hw.functions.data.model.City;
import cz.cvut.fel.omo.hw.functions.data.model.VoterTurnout;
import cz.cvut.fel.omo.hw.functions.data.model.Vote;
import cz.cvut.fel.omo.hw.functions.data.model.Region;

import cz.cvut.fel.omo.hw.functions.utils.CandidateUtils;
import cz.cvut.fel.omo.hw.functions.utils.CandidateUtilsImpl;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NationalStatisticsImpl implements NationalStatistics {

    private final CompletableFuture<List<RegionResults>> regionResults;
    private final CandidateUtils candidateUtils;

    public NationalStatisticsImpl(CompletableFuture<List<RegionResults>> regionResults, CompletableFuture<Candidates> candidates) {
        this.regionResults = regionResults;
        this.candidateUtils = new CandidateUtilsImpl(candidates);
    }

    @Override
    public String getNameOfCityWithTheLowestVoterTurnout() {
        return regionResults.join().stream()
                .flatMap(region -> region.getRegions().stream())
                .flatMap(region -> region.getDistricts().stream())
                .flatMap(district -> district.getCities().stream())
                .min(Comparator.comparingDouble(city -> (double) city.getVoterTurnout().getNumberOfSubmittedVotingEnvelopes() / city.getVoterTurnout().getNumberOfRegisteredVoters()
                ))
                .map(City::getName)
                .orElse("Fail");
    }

    private Stream<City> getAllCitiesFromRegions(List<RegionResults> regions) {
        return regions.stream()
                .flatMap(regionResult -> regionResult.getRegions().stream())
                .flatMap(region -> region.getDistricts().stream())
                .flatMap(district -> district.getCities().stream());
    }

    private double calculateNonValidVotesRatio(VoterTurnout turnout) {
        return turnout != null && turnout.getNumberOfSubmittedVotingEnvelopes() > 0
                ? (double) (turnout.getNumberOfSubmittedVotingEnvelopes() - turnout.getNumberOfValidVotes()) /
                turnout.getNumberOfSubmittedVotingEnvelopes()
                : 0.0;
    }

    @Override
    public String getNameOfCityWithTheHighestNonValidVotesRatio() {
        return getAllCitiesFromRegions(regionResults.join())
                .filter(city -> city.getVoterTurnout() != null)
                .max(Comparator.comparingDouble(city -> calculateNonValidVotesRatio(city.getVoterTurnout())))
                .map(City::getName)
                .orElse("Fail");
    }

    private boolean hasCandidateWonOrDraw(City city, int candidateId) {
        int maxVotes = city.getVotes().stream()
                .mapToInt(Vote::getVotes)
                .max()
                .orElse(0);

        return city.getVotes().stream()
                .filter(vote -> vote.getVotes() == maxVotes)
                .anyMatch(vote -> vote.getCandidateId() == candidateId);
    }


    private int getCandidateVotesInCity(City city, int candidateId) {
        return city.getVotes().stream()
                .filter(vote -> vote.getCandidateId() == candidateId)
                .mapToInt(Vote::getVotes)
                .sum();
    }

    @Override
    public List<String> getTop10CitiesWhereCandidateWonOrderedByNumberOfVotesDesc(int candidateId) {
        return regionResults.join().stream()
                .flatMap(regionResult -> regionResult.getRegions().stream())
                .flatMap(region -> region.getDistricts().stream())
                .flatMap(district -> district.getCities().stream())
                .filter(city -> hasCandidateWonOrDraw(city, candidateId))
                .sorted(Comparator.comparingInt((City city) -> getCandidateVotesInCity(city, candidateId))
                        .reversed())
                .limit(10)
                .map(City::getName)
                .toList();
    }




    private String determineRegionWinner(Region region) {
        return region.getDistricts().stream()
                .flatMap(district -> district.getCities().stream())
                .flatMap(city -> city.getVotes().stream())
                .collect(Collectors.groupingBy(Vote::getCandidateId, Collectors.summingInt(Vote::getVotes)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .flatMap(entry -> candidateUtils.getCandidateFullName((entry.getKey())))
                .orElse("No determineRegionWinner(Region region)");
    }
    @Override
    public Map<String, String> getRegionWinnerMap() {
        return regionResults.join().stream()
                .flatMap(regionResult -> regionResult.getRegions().stream())
                .collect(Collectors.toMap(
                        Region::getName,
                        this::determineRegionWinner
                ));
    }

}
