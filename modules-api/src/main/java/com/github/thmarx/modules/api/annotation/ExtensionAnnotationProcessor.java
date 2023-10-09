package com.github.thmarx.modules.api.annotation;

/*-
 * #%L
 * modules-api
 * %%
 * Copyright (C) 2023 Thorsten Marx
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import com.google.auto.service.AutoService;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes("com.github.thmarx.modules.api.annotation.Extension")
@AutoService(Processor.class)
public class ExtensionAnnotationProcessor extends AbstractProcessor implements Processor {

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return false;
		}
		Map<String, Set<String>> extensions = new HashMap<>();

		Elements elements = processingEnv.getElementUtils();

		roundEnv.getElementsAnnotatedWith(Extension.class).stream().forEach((e) -> {
			Extension a = e.getAnnotation(Extension.class);
			if (!(a == null)) {
				if (!(!e.getKind().isClass() && !e.getKind().isInterface())) {
					TypeElement typedElement = (TypeElement) e;
					Collection<TypeElement> teCollection = getTypeElements(typedElement, a);
					teCollection.forEach((te) -> {
						String extensionName = elements.getBinaryName(te).toString();
						String extensionImplName = elements.getBinaryName(typedElement).toString();
						if (!extensions.containsKey(extensionName)) {
							extensions.put(extensionName, new TreeSet<>());
						}
						extensions.get(extensionName).add(extensionImplName);
					});
				}
			}
		});

		// load existing extensions
		final Filer filer = processingEnv.getFiler();
		extensions.entrySet().stream().forEach((e) -> {
			try {
				String contract = e.getKey();
				FileObject extensionFileObject = filer.getResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/" + contract);
				BufferedReader r = new BufferedReader(new InputStreamReader(extensionFileObject.openInputStream(), "UTF-8"));
				String line;
				while ((line = r.readLine()) != null) {
					e.getValue().add(line);
				}
				r.close();
			} catch (FileNotFoundException fnfe) {
				// file not found
			} catch (IOException x) {
				//x.printStackTrace();
				if (NoSuchFileException.class.isInstance(x)) {
					// file not found
				} else {
					//processingEnv.getMessager().printMessage(Kind.ERROR, "Error loading existing extension files: " + x);
				}
			}
		});

		// now write them back out
		extensions.entrySet().stream().forEach((e) -> {
			try {
				String extension = e.getKey();
				processingEnv.getMessager().printMessage(Kind.NOTE, "Creating META-INF/services/" + extension);
				FileObject f = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/" + extension);
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(f.openOutputStream(), "UTF-8"));
				for (String value : e.getValue()) {
					pw.println(value);
				}
				pw.close();
			} catch (IOException x) {
				processingEnv.getMessager().printMessage(Kind.ERROR, "Error creating extension file: " + x);
			}
		});

		return false;
	}

	private Collection<TypeElement> getTypeElements(TypeElement type, Extension a) {
		List<TypeElement> typeElements = new ArrayList<>();

		try {
			a.value();
		} catch (MirroredTypesException e) {

			e.getTypeMirrors().stream().forEach((m) -> {
				if (m instanceof DeclaredType) {
					DeclaredType dt = (DeclaredType) m;
					typeElements.add((TypeElement) dt.asElement());
				} else {
					processingEnv.getMessager().printMessage(Kind.ERROR, "Invalid type specified", type);
				}
			});
		}
		return typeElements;
	}

}
