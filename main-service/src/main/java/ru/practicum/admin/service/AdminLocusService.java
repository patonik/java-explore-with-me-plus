package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.repository.AdminLocusRepository;
import ru.practicum.dto.locus.LocusMapper;
import ru.practicum.dto.locus.LocusUpdateDto;
import ru.practicum.dto.locus.NewLocusDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Locus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminLocusService {
    private final AdminLocusRepository adminLocusRepository;
    private final LocusMapper locusMapper;

    public void deleteLocus(Long locusId) {
        adminLocusRepository.deleteById(locusId);
    }

    public Locus addLocus(NewLocusDto locus) {
        return adminLocusRepository.save(locusMapper.toLocus(locus));
    }

    public List<Locus> getAllLoci() {
        return adminLocusRepository.findAll();
    }

    public Locus getLocusById(Long locusId) {
        return adminLocusRepository.findById(locusId).orElseThrow(() -> new NotFoundException("Locus not found"));
    }

    @Transactional()
    public Locus updateLocus(Long locusId, LocusUpdateDto locusUpdateDto) {
        Locus loc = adminLocusRepository.findById(locusId).orElseThrow(() -> new NotFoundException("Locus not found"));
        loc = locusMapper.updateLocus(loc, locusUpdateDto);
        return adminLocusRepository.save(loc);
    }
}
