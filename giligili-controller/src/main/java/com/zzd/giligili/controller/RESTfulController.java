package com.zzd.giligili.controller;

import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/18 12:52
 */
@RestController
public class RESTfulController {
    private final Map<Integer, Map<String, Object>> hashMap;

    public RESTfulController(){
        hashMap = new HashMap<>();
        for (int i = 1; i < 3; i++) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("id",i);
            tempMap.put("name","name" + i);
            hashMap.put(i,tempMap);
        }
    }

    @GetMapping("/objects/{id}")
    public Map<String, Object> getData(@PathVariable Integer id){
        return  hashMap.get(id);
    }

    @DeleteMapping("/objects/{id}")
    public String deleteData(Integer id){
        hashMap.remove(id);
        return "delete success";
    }

    @PostMapping("/objects")
    public String postData(@RequestBody Map<String, Object> map){
        int size = hashMap.size();
        hashMap.put(size + 1,map);
        return "post success";
    }

    @PutMapping("/objects")
    public String putData(@RequestBody Map<String, Object> map){
        Integer id = Integer.valueOf(String.valueOf(map.get("id")));
        Map<String, Object> contains = hashMap.get(id);
        if (contains == null){
            int size = hashMap.size();
            hashMap.put(size + 1,map);
        } else {
            hashMap.put(id,map);
        }
        return "put success";
    }
}
