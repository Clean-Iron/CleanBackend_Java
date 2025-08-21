package co.cleaniron.service;

import co.cleaniron.model.Address;
import co.cleaniron.model.Client;
import co.cleaniron.model.dto.AddressDto;
import co.cleaniron.model.dto.ClientDto;
import co.cleaniron.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<ClientDto> getClients() {
        return clientRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientDto getClientById(String doc) {
        Client c = clientRepository.findByDocument(doc)
                .orElseThrow(() -> new NoSuchElementException("Cliente no existe: " + doc));
        return toDTO(c);
    }

    public Client getClientWithAddresses(String doc) {
        return clientRepository.findByDocumentWithAddresses(doc).orElse(null);
    }

    public List<Client> getClientsByCity(String city) {
        return clientRepository.findAllClientsWithAddressInACity(city);
    }

    @Transactional
    public Boolean updateClient(String document, Client updatedClient) {
        // usa el método que trae las direcciones
        Client existingClient = clientRepository.findByDocumentWithAddresses(document)
                .orElse(null);
        if (existingClient == null) return false;

        // campos básicos
        existingClient.setTypeId(updatedClient.getTypeId());
        existingClient.setName(updatedClient.getName());
        existingClient.setSurname(updatedClient.getSurname());
        existingClient.setPhone(updatedClient.getPhone());
        existingClient.setEmail(updatedClient.getEmail());

        // sincronizar direcciones
        if (updatedClient.getAddresses() != null) {
            var existing = existingClient.getAddresses();
            var toRemove = new HashSet<>(existing);

            for (Address inAddr : updatedClient.getAddresses()) {
                if (inAddr.getId() != null) {
                    Address match = existing.stream()
                            .filter(a -> a.getId().equals(inAddr.getId()))
                            .findFirst().orElse(null);
                    if (match != null) {
                        match.setAddress(inAddr.getAddress());
                        match.setCity(inAddr.getCity());
                        match.setDescription(inAddr.getDescription());
                        toRemove.remove(match);
                    } else {
                        inAddr.setClient(existingClient);
                        existing.add(inAddr);
                    }
                } else {
                    inAddr.setClient(existingClient);
                    existing.add(inAddr);
                }
            }

            // con orphanRemoval=true basta con quitar del Set
            toRemove.forEach(existing::remove);
        }

        // no necesitas llamar a save() si la entidad está managed,
        // pero puedes dejarlo si quieres explícito:
        clientRepository.save(existingClient);
        return true;
    }


    public Boolean saveClient(Client client) {
        if (clientRepository.findById(client.getDocument()).isEmpty()) {
            clientRepository.save(client);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteClient(String document) {
        if (!clientRepository.existsById(document)) {
            return false;
        }
        clientRepository.deleteById(document);
        return true;
    }

    private ClientDto toDTO(Client c) {
        Set<AddressDto> addrs = c.getAddresses().stream()
                .map(a -> new AddressDto(a.getId(), a.getAddress(), a.getCity(), a.getDescription()))
                .collect(java.util.stream.Collectors.toSet());
        return new ClientDto(c.getDocument(), c.getTypeId(), c.getName(), c.getSurname(),
                c.getPhone(), c.getEmail(), addrs);
    }
}
