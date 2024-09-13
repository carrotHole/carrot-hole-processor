package io.github.carrothole.processor.generateo.service;

import io.github.carrothole.processor.generateo.anno.AppendField;
import io.github.carrothole.processor.generateo.entity.ClassInfo;
import io.github.carrothole.processor.generateo.entity.FieldInfo;
import io.github.carrothole.processor.generateo.enums.VOTypeEnum;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.util.Locale.ENGLISH;

/**
 * Description: processor基类 <br>
 * Date: 2024/9/4 15:44 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public interface ProcessorService<T> {


}
