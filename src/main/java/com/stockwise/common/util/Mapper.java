package com.stockwise.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockwise.common.constant.ExceptionMessage;
import com.stockwise.common.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public <T> T convert(Object srcObj, Class<T> targetClass) {
        try {

            return modelMapper.map(srcObj, targetClass);

        } catch (IllegalArgumentException argumentException) {

            throw new CustomException(ExceptionMessage.SOURCE_OR_DESTINATION_IS_NULL, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (MappingException | ConfigurationException eRuntimeException) {

            throw new CustomException(eRuntimeException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public <S, T> List<T> convertToList(List<S> srcList, Class<T> targetClass) {
        List<T> response = new ArrayList<>();

        if (srcList != null) {
            srcList.stream().forEach(source -> response.add(convert(source, targetClass)));
        }

        return response;
    }

    public <T> T convertInputStream(InputStream inputStream, Class<T> clazz) {
		try {
			return objectMapper.readValue(inputStream, clazz);
		} catch (IOException ex) {
			throw new CustomException(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
