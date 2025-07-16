package co.cleaniron.controller;

import co.cleaniron.model.Client;
import co.cleaniron.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{id}")
    public Client getClientById(@PathVariable String id) {
        return clientService.getClientById(id);
    }

    @GetMapping("addresses/{doc}")
    public ResponseEntity<Client> getClientAddresses(@PathVariable("doc") String doc){
        return ResponseEntity.ok(clientService.getClientWithAddresses(doc));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities(){
        return ResponseEntity.ok(clientService.getCities());
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients(){
        return ResponseEntity.ok(clientService.getClients());
    }

    @GetMapping("clientsByCity/{city}")
    public ResponseEntity<List<Client>> getClientsByCity(@PathVariable("city") String city){
        return ResponseEntity.ok(clientService.getClientsByCity(city));
    }

    @PostMapping
    public ResponseEntity<Boolean> createClient(@RequestBody Client client){
        return ResponseEntity.ok(clientService.saveClient(client));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateClient(@PathVariable("id") String id, @RequestBody Client updatedClient) {
        return ResponseEntity.ok(clientService.updateClient(id, updatedClient));
    }

}
