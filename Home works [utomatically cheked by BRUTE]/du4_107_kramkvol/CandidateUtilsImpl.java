package cz.cvut.fel.omo.hw.functions.utils;

import cz.cvut.fel.omo.hw.functions.data.model.Candidate;
import cz.cvut.fel.omo.hw.functions.data.model.Candidates;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Log
@RequiredArgsConstructor
public class CandidateUtilsImpl implements CandidateUtils {

    private final CompletableFuture<Candidates> candidates;

    @Override
    public Optional<String> getCandidateFullName(int id) {
        return getCandidate(id).map(Candidate::getFullName);
    }

    @Override
    public Optional<Integer> getCandidateAge(int id) {
        return getCandidate(id).map(Candidate::getAge);
    }

    @Override
    public <T> Optional<T> getCandidateAttribute(int id, Function<Candidate, T> mappingFunction) {
        return getCandidate(id).map(mappingFunction);
    }

    @Override
    public Optional<Candidate> getCandidate(int id) {
        return candidates.join().getCandidatesList().stream()
                .filter(candidate -> candidate.getId() == id)
                .findFirst();
    }
}
