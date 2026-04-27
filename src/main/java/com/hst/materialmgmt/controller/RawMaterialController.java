package com.hst.materialmgmt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hst.api.RawmaterialApi;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Supplier API", description = "Endpoints for supplier operations")
public class RawMaterialController extends BaseController implements RawmaterialApi{
	
}


