package com.calero.lili.api.controllers;

import com.calero.lili.core.modCargarExcel.services.ExcelCargaGastosServiceImpl;
import com.calero.lili.core.modCargarExcel.services.ExcelCargaItemsServiceImpl;
import com.calero.lili.core.modCargarExcel.services.ExcelCargaMarcasServiceImpl;
import com.calero.lili.core.modCargarExcel.services.ExcelCargaMedidasServiceImpl;
import com.calero.lili.core.modCargarExcel.services.ExcelCargaServiciosServiceImpl;
import com.calero.lili.core.modCargarExcel.services.ExcelCargarCategoriasServiceImpl;
import com.calero.lili.core.modCargarExcel.services.ExcelCargarTercerosServiceImpl;
import com.calero.lili.core.modCargarExcel.services.ExcelCargarTrabajadoresServiceImpl;
import com.calero.lili.core.modTesoreria.modTesoreriaEstadosCuenta.ExcelCargarEstadoCuentaServiceImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/cargar-excel")
@CrossOrigin(originPatterns = "*")

public class ExcelCargarController {

    private final ExcelCargarTercerosServiceImpl clientesService;
    private final ExcelCargaItemsServiceImpl excelCargaItemsService;
    private final ExcelCargaServiciosServiceImpl cargarItemsServicios;
    private final ExcelCargaGastosServiceImpl excelCargaGastosService;
    private final ExcelCargaMarcasServiceImpl excelCargaMarcasService;
    private final ExcelCargarCategoriasServiceImpl excelCargarCategoriasService;
    private final ExcelCargaMedidasServiceImpl excelCargaMedidasService;
    private final ExcelCargarEstadoCuentaServiceImpl excelCargarEstadoCuentaService;
    private final ExcelCargarTrabajadoresServiceImpl excelCargarTrabajadoresService;

    private final IdDataServiceImpl idDataService;

    @PostMapping("/terceros")
    public void uploadFileClientes(@RequestBody MultipartFile file) {
        try {
            clientesService.carga(idDataService.getIdData(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/items/productos/{idEmpresa}")
    public void uploadFileItemsProductos(@RequestBody MultipartFile file, @PathVariable("idEmpresa") Long idEmpresa) {
        try {
            excelCargaItemsService.cargarItemsProductos(idDataService.getIdData(), file, idEmpresa);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/items/servicios/{idEmpresa}")
    public void uploadFileItemsServicios(@RequestBody MultipartFile file, @PathVariable("idEmpresa") Long idEmpresa) {
        try {
            cargarItemsServicios.cargarItemsServicios(idDataService.getIdData(), file, idEmpresa);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/items/gastos/{idEmpresa}")
    public void uploadFileItemsGastos(@RequestBody MultipartFile file, @PathVariable("idEmpresa") Long idEmpresa) {
        try {
            excelCargaGastosService.cargarItemsGastos(idDataService.getIdData(), file, idEmpresa);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/marcas")
    public void uploadFileMarcas(@RequestBody MultipartFile file) {
        try {
            excelCargaMarcasService.cargarMarcas(idDataService.getIdData(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/categorias")
    public void uploadFileCategorias(@RequestBody MultipartFile file) {
        try {
            excelCargarCategoriasService.cargarCategoria(idDataService.getIdData(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/medidas")
    public void uploadFileMedidas(@RequestBody MultipartFile file) {
        try {
            excelCargaMedidasService.cargarMedidas(idDataService.getIdData(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/estado-cuenta")
    public void uploadFileEstadoCuenta(@RequestParam("file") MultipartFile file,
                                       @RequestParam("periodo") String periodo,
                                       @RequestParam("idEntidad") String idEntidad) {
        try {
            excelCargarEstadoCuentaService.cargarEstadoCuenta(file, UUID.fromString(idEntidad), periodo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/trabajadores/{idEmpresa}")
    public void uploadFileTrabajadores(@RequestParam("file") MultipartFile file,
                                       @PathVariable("idEmpresa") Long idEmpresa) {
        try {
            excelCargarTrabajadoresService.cargarTrabajadores(idDataService.getIdData(), file, idEmpresa);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
