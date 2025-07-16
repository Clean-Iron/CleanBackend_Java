package co.cleaniron.service;

import co.cleaniron.model.Address;
import co.cleaniron.model.Client;
import co.cleaniron.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client getClientById(String doc) {
        return (clientRepository.findById(doc).isPresent() ? clientRepository.findById(doc).get() : null);
    }

    public Client getClientWithAddresses(String doc) {
        return clientRepository.findByDocumentWithAddresses(doc).orElse(null);
    }

    public List<Client> getClientsByCity(String city) {
        return clientRepository.findAllClientsWithAddressInACity(city);
    }

    public List<String> getCities(){
        return clientRepository.findAllCities();
    }

    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    public Boolean updateClient(String document, Client updatedClient) {
        Optional<Client> existingClientOpt = clientRepository.findById(document);

        if (existingClientOpt.isPresent()) {
            Client existingClient = existingClientOpt.get();

            System.out.println(updatedClient.getTypeId());
            // Actualizar campos básicos del cliente
            existingClient.setTypeId(updatedClient.getTypeId());
            existingClient.setName(updatedClient.getName());
            existingClient.setSurname(updatedClient.getSurname());
            existingClient.setPhone(updatedClient.getPhone());
            existingClient.setEmail(updatedClient.getEmail());

            // --------- SINCRONIZACIÓN DE DIRECCIONES ---------
            if (updatedClient.getAddresses() != null) {
                Set<Address> incomingAddresses = updatedClient.getAddresses();
                Set<Address> existingAddresses = existingClient.getAddresses();

                // Crear copia para evitar ConcurrentModificationException
                Set<Address> addressesToRemove = new HashSet<>(existingAddresses);

                for (Address incoming : incomingAddresses) {
                    if (incoming.getId() != null) {
                        // Buscar dirección existente por ID
                        Address match = existingAddresses.stream()
                                .filter(a -> incoming.getId().equals(a.getId()))
                                .findFirst()
                                .orElse(null);

                        if (match != null) {
                            // Actualizar campos de la dirección existente
                            match.setAddress(incoming.getAddress());
                            match.setCity(incoming.getCity());
                            match.setDescription(incoming.getDescription());
                            addressesToRemove.remove(match);
                        } else {
                            // Dirección con ID pero no se encuentra en la lista actual: agregarla
                            incoming.setClient(existingClient);
                            existingAddresses.add(incoming);
                        }
                    } else {
                        // Nueva dirección (sin ID): agregar
                        incoming.setClient(existingClient);
                        existingAddresses.add(incoming);
                    }
                }

                // Eliminar direcciones que no llegaron en la nueva lista
                for (Address addressToRemove : addressesToRemove) {
                    addressToRemove.setClient(null); // rompe relación
                    existingAddresses.remove(addressToRemove);
                }
            }

            clientRepository.save(existingClient);
            return true;
        }

        return false;
    }


    public Boolean saveClient(Client client) {
        if (clientRepository.findById(client.getDocument()).isEmpty()) {
            clientRepository.save(client);
            return true;
        }
        return false;
    }
}
