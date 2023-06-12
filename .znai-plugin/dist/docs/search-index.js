znaiSearchData = [["@@index@@","","","","Note: It is very important to not overuse signs. Make sure each sign counts.Warning: Bring attention to a common mistake or an often missed configuration step using a warning sign. Do not use too many warning signs.Question: Use the question sign to bring an extra attention to the main idea of a page. \\ \\ What is the point of the attention signs ?Exercise: write a hello world example in this languageAvoid: using multiple versions of ReactJS inside one project.Don't: commit node_modules to your repositoryDo not: commit node_modules to your repositoryTip: use temporary directory to generate the summary file for uploadRecommendation: write automated tests for new business logic"]]
/*
 * Copyright 2019 TWO SIGMA OPEN SOURCE, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

znaiSearchIdx = lunr(function () {
    this.ref('id')
    this.field('section')
    this.field('pageTitle')
    this.field('pageSection')
    this.field('text')

    this.metadataWhitelist = ['position']

    znaiSearchData.forEach(function (e) {
        this.add({
            id: e[0],
            section: e[1],
            pageTitle: e[2],
            pageSection: e[3],
            text: e[4],
        })
    }, this)
})
