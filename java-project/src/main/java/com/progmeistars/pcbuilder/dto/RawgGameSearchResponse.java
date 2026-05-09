package com.progmeistars.pcbuilder.dto;

import java.util.List;

public class RawgGameSearchResponse {
    private Integer count;
    private String next;
    private String previous;
    private List<RawgGameDTO> results;

    public RawgGameSearchResponse() {
    }

    public RawgGameSearchResponse(Integer count, String next, String previous, List<RawgGameDTO> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<RawgGameDTO> getResults() {
        return results;
    }

    public void setResults(List<RawgGameDTO> results) {
        this.results = results;
    }
}
