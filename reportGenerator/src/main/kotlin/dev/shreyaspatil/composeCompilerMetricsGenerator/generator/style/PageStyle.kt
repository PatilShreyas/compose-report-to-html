/**
 * MIT License
 *
 * Copyright (c) 2022 Shreyas Patil
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style

import kotlinx.html.HEAD
import kotlinx.html.style
import kotlinx.html.unsafe

fun HEAD.PageStyle() {
    style {
        unsafe {
            raw(
                """
                    body {
                        font-family: 'Roboto', sans-serif;
                        text-align:center
                    }
                    
                    hr {
                        margin: 8px;
                    }
                    
                    table, th, td {
                        border: 1px solid black;
                        border-collapse: collapse;
                        margin: 8px;
                        padding: 4px
                    }
                    
                    .code {
                        font-family: 'Roboto Mono', monospace;
                    }
                    
                    .collapsible {
                        background-color: #777;
                        color: white;
                        cursor: pointer;
                        padding: 18px;
                        width: 100%;
                        border: none;
                        text-align: left;
                        outline: none;
                        font-size: 24px;
                        font-weight: 600;
                    }

                    .active, .collapsible:hover {
                        background-color: #555;
                    }

                    .collapsible:after {
                        content: '\002B';
                        color: white;
                        font-weight: bold;
                        float: right;
                        margin-left: 5px;
                    }

                    .active:after {
                        content: "\2212";
                    }

                    .content {
                        padding: 0 18px;
                        max-height: 0;
                        overflow: hidden;
                        transition: max-height 0.2s ease-out;
                        background-color: #f1f1f1;
                    }
                    
                     .content-header {
                        background-color: #777;
                        color: white;
                        cursor: pointer;
                        padding: 18px;
                        width: 100%;
                        border: none;
                        text-align: left;
                        outline: none;
                        font-size: 24px;
                        font-weight: 600;
                    }
                    
                    .content-body {
                        padding: 0 18px;
                        transition: max-height 0.2s ease-out;
                        background-color: #f1f1f1;
                    }
                    
                    .center {
                        margin-left: auto;
                        margin-right: auto;
                    }
                    
                    footer {
                        padding: 16px;
                        background-color: #E0E0E0 
                    }
                    
                    .status-success {
                        color: ${Colors.GREEN_DARK};
                    }
                    
                    .status-failure {
                        color: ${Colors.RED_DARK};
                    }
                    
                    .background-status-success {
                        background-color: ${Colors.GREEN_LIGHT};
                        color: ${Colors.BLACK};
                    }
                    
                    .background-status-failure {
                        background-color: ${Colors.RED_DARK};
                        color: ${Colors.WHITE}
                    }
                    
                    .empty-content {
                        margin: 4px;
                        padding: 18px;
                        text-align: center;
                        font-size: 18px;
                        font-weight: 600;
                    }
                """.trimIndent()
            )
        }
    }
}

fun statusCssClass(success: Boolean) = if (success) "status-success" else "status-failure"
fun backgroundStatusCssClass(success: Boolean) =
    if (success) "background-status-success" else "background-status-failure"
