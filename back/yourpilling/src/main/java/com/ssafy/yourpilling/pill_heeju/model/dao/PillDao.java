package com.ssafy.yourpilling.pill_heeju.model.dao;

import com.ssafy.yourpilling.pill_heeju.model.dao.entity.NutritionDetail;
import com.ssafy.yourpilling.pill_heeju.model.dao.entity.PillDetail;

import java.util.List;

public interface PillDao {

    PillDetail pillDetail(PillDetail pillDetail);
    List<PillDetail> pillSearchList(PillDetail pillDetail);
    List<PillDetail> pillSearchListByNutrition(NutritionDetail nutritionDetail);

    List<PillDetail> pillSearchListByHealthConcern(List<Integer> categories);
}
