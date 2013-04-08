/*
 * Copyright 2010 JBoss Inc
 *
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
 */

package org.optaplanner.examples.nurserostering.persistence;

import org.optaplanner.examples.common.persistence.XStreamSolutionDao;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

public class NurseRosteringDao extends XStreamSolutionDao {

    public NurseRosteringDao() {
        super("nurserostering", NurseRoster.class);
    }

}