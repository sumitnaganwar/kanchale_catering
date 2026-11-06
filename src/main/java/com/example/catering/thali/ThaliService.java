package com.example.catering.thali;

import com.example.catering.food.FoodItem;
import com.example.catering.food.FoodRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ThaliService {
    private final ThaliRepository thalis;
    private final FoodRepository foods;
    public ThaliService(ThaliRepository thalis, FoodRepository foods) { this.thalis = thalis; this.foods = foods; }

    @Transactional(readOnly = true)
    public List<ThaliResponse> publicList() { return thalis.findAllByAvailableTrueOrderByNameAsc().stream().map(ThaliResponse::from).toList(); }

    @Transactional(readOnly = true)
    public ThaliResponse publicGet(Long id) {
        Thali thali = thalis.findById(id).filter(Thali::isAvailable)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thali not found"));
        return ThaliResponse.from(thali);
    }

    @Transactional(readOnly = true)
    public List<ThaliResponse> adminList() { return thalis.findAllByOrderByNameAsc().stream().map(ThaliResponse::from).toList(); }

    @Transactional
    public ThaliResponse create(ThaliRequest request) {
        Thali thali = new Thali(request.name(), request.description(), request.pricePerPlate(), request.image(), resolveFoods(request.foodIds()), request.available());
        return ThaliResponse.from(thalis.save(thali));
    }

    @Transactional
    public ThaliResponse update(Long id, ThaliRequest request) {
        Thali thali = thalis.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thali not found"));
        thali.update(request.name(), request.description(), request.pricePerPlate(), request.image(), resolveFoods(request.foodIds()), request.available());
        return ThaliResponse.from(thalis.save(thali));
    }

    @Transactional
    public void delete(Long id) {
        Thali thali = thalis.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thali not found"));
        thalis.delete(thali);
    }

    private List<FoodItem> resolveFoods(List<Long> ids) {
        Map<Long, FoodItem> byId = foods.findAllById(ids).stream().collect(Collectors.toMap(FoodItem::getId, Function.identity()));
        if (byId.size() != ids.stream().distinct().count()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more selected menu foods do not exist");
        if (byId.values().stream().anyMatch(food -> !food.isAvailable())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Remove unavailable foods from this thali before saving");
        return ids.stream().distinct().map(byId::get).toList();
    }
}
