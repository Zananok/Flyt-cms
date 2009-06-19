/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var prevRow = null;
var prevCls = null;
function selectRow(row) {
   if (prevRow != null) {
      var prev = document.getElementById("row" + prevRow);
      prev.className = prevCls;
   }

   var current = document.getElementById("row" + row);
   if (current != null) {
      prevRow = row;
      prevCls = current.className;
      current.className = "tableRowSel";
   }

   return document.myform.elements['id' + row].value;
}

function getSelectedValue(list)
{
   if (!list) return "";

   for (var i = 0; i < list.length; i++) {
      if (list.options[i].selected) {
         return list.options[i].value;
      }
   }
   return "";
}


function isChecked(elm) {
    if (!elm) return false;
    if (!elm.length) return true;

    for (var i = 0; i < elm.length; i++) {
        if (elm[i].checked) return true;
    }

    return false;
}
