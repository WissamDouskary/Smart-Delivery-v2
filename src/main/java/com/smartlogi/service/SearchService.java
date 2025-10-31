package com.smartlogi.service;

import com.smartlogi.dto.responseDTO.*;
import com.smartlogi.mapper.ColisMapper;
import com.smartlogi.mapper.LivreurMapper;
import com.smartlogi.mapper.ReceiverMapper;
import com.smartlogi.mapper.SenderMapper;
import com.smartlogi.repository.ColisRepository;
import com.smartlogi.repository.LivreurRepository;
import com.smartlogi.repository.ReceiverRepository;
import com.smartlogi.repository.SenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final ColisRepository colisRepository;
    private final SenderRepository senderRepository;
    private final ReceiverRepository receiverRepository;
    private final LivreurRepository livreurRepository;
    private final ColisMapper colisMapper;
    private final SenderMapper senderMapper;
    private final ReceiverMapper receiverMapper;
    private final LivreurMapper livreurMapper;

    @Autowired
    public SearchService(
            ColisRepository colisRepository, SenderRepository senderRepository, ReceiverRepository receiverRepository,
            LivreurRepository livreurRepository, ColisMapper colisMapper, SenderMapper senderMapper, ReceiverMapper receiverMapper,
            LivreurMapper livreurMapper
    ){
        this.colisMapper = colisMapper;
        this.colisRepository = colisRepository;
        this.senderRepository = senderRepository;
        this.receiverRepository = receiverRepository;
        this.livreurRepository = livreurRepository;
        this.senderMapper = senderMapper;
        this.receiverMapper = receiverMapper;
        this.livreurMapper = livreurMapper;
    }

    public SearchResponseDTO searchAll(String keyword) {

        List<ColisResponseDTO> colisList = colisRepository
                .findByDescriptionContainingIgnoreCaseOrVileDistinationContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(colisMapper::toDTO)
                .toList();

        List<SenderResponseDTO> senderList = senderRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword)
                .stream()
                .map(senderMapper::toResponseDTO)
                .toList();

        List<ReceiverResponseDTO> receiverList = receiverRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword)
                .stream()
                .map(receiverMapper::toResponseDTO)
                .toList();

        List<LivreurResponseDTO> livreurList = livreurRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrTelephoneContainingIgnoreCaseOrCity_NomContainingIgnoreCase(keyword, keyword, keyword, keyword)
                .stream()
                .map(livreurMapper::toDTO)
                .toList();

        SearchResponseDTO searchResponseDTO = new SearchResponseDTO();

        searchResponseDTO.setColis(colisList);
        searchResponseDTO.setSenders(senderList);
        searchResponseDTO.setReceivers(receiverList);
        searchResponseDTO.setLivreurs(livreurList);

        return searchResponseDTO;
    }
}
