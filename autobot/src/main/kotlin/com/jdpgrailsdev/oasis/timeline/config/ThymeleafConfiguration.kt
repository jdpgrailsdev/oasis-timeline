/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jdpgrailsdev.oasis.timeline.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import java.nio.charset.Charset

/** Spring configuration for Thymeleaf related beans. */
@Suppress("UNUSED")
@Configuration
class ThymeleafConfiguration {
  /**
   * Defines the text-based [TemplateEngine] bean.
   *
   * @return The text-based [TemplateEngine] bean.
   */
  @Bean(name = ["textTemplateEngine"])
  fun textTemplateEngine(): TemplateEngine {
    val templateEngine = SpringTemplateEngine()
    templateEngine.setTemplateResolver(textTemplateResolver())
    templateEngine.enableSpringELCompiler = true
    return templateEngine
  }

  private fun textTemplateResolver(): ITemplateResolver {
    val templateResolver = ClassLoaderTemplateResolver()
    templateResolver.prefix = "templates/text/"
    templateResolver.suffix = ".txt"
    templateResolver.templateMode = TemplateMode.TEXT
    templateResolver.characterEncoding = Charset.defaultCharset().name()
    templateResolver.checkExistence = true
    templateResolver.isCacheable = true
    return templateResolver
  }
}
