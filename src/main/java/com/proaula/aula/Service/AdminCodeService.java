package com.proaula.aula.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proaula.aula.document.AdminCode;
import com.proaula.aula.Repository.mongodb.AdminCodeRepository;

@Service
public class AdminCodeService {

    @Autowired
    private AdminCodeRepository adminCodeRepository;

    public boolean isValidAdminCode(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }
        return adminCodeRepository.existsByCodigo(codigo.trim());
    }

    public AdminCode findByCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        return adminCodeRepository.findByCodigo(codigo.trim()).orElse(null);
    }

    public void ensureDefaultAdminCodes(List<String> defaultCodes) {
        if (defaultCodes == null || defaultCodes.isEmpty()) {
            return;
        }

        for (String codigo : defaultCodes) {
            if (codigo == null || codigo.trim().isEmpty()) {
                continue;
            }
            String trimmedCode = codigo.trim();
            if (!adminCodeRepository.existsByCodigo(trimmedCode)) {
                AdminCode adminCode = new AdminCode();
                adminCode.setCodigo(trimmedCode);
                adminCode.setDescripcion("Código generado automáticamente");
                adminCode.setFechaCreacion(System.currentTimeMillis());
                adminCode.setActivo(true);
                adminCodeRepository.save(adminCode);
            }
        }
    }
}
