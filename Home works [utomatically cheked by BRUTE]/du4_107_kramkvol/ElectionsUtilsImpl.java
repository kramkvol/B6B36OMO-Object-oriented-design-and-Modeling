package cz.cvut.fel.omo.hw.functions.utils;

import cz.cvut.fel.omo.hw.functions.data.model.AbroadResults;
import cz.cvut.fel.omo.hw.functions.data.model.RegionResults;
import cz.cvut.fel.omo.hw.functions.data.model.Vote;
import cz.cvut.fel.omo.hw.functions.data.model.VoterTurnout;
import cz.cvut.fel.omo.hw.functions.data.model.Country;
import cz.cvut.fel.omo.hw.functions.data.model.City;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ElectionsUtilsImpl implements ElectionsUtils {

    private final CompletableFuture<List<RegionResults>> regionResultsFuture;
    private final CompletableFuture<AbroadResults> abroadResultsFuture;

    @Override
    public List<Vote> getAllVotes() {
        return Stream.concat(
                getRegionVotesStream(),
                getAbroadVotesStream()
        ).toList();
    }

    @Override
    public List<VoterTurnout> getAllVoterTurnouts() {
        return Stream.concat(
                getRegionVoterTurnoutsStream(),
                getAbroadVoterTurnoutStream()
        ).toList();
    }

    private Stream<Vote> getRegionVotesStream() {
        return regionResultsFuture.join().stream()
                .flatMap(regionResult -> regionResult.getRegions().stream())
                .flatMap(region -> region.getDistricts().stream())
                .flatMap(district -> district.getCities().stream())
                .flatMap(city -> city.getVotes().stream());
    }

    private Stream<Vote> getAbroadVotesStream() {
        return abroadResultsFuture.join().getAbroad().getContinents().stream()
                .flatMap(region -> region.getCountries().stream())
                .flatMap(country -> country.getVotes().stream());
    }
    private Stream<VoterTurnout> getAbroadVoterTurnoutStream() {
        return abroadResultsFuture.join().getAbroad().getContinents().stream()
                .flatMap(continent -> continent.getCountries().stream())
                .map(Country::getVoterTurnout)
                .filter(java.util.Objects::nonNull);
    }

    private Stream<VoterTurnout> getRegionVoterTurnoutsStream() {
        return regionResultsFuture.join().stream()
                .flatMap(regionResult -> regionResult.getRegions().stream())
                .flatMap(region -> region.getDistricts().stream())
                .flatMap(district -> district.getCities().stream())
                .map(City::getVoterTurnout)
                .filter(java.util.Objects::nonNull);
    }
}
