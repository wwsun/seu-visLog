(function($){

    /**
     *
     * CSV Parser credit goes to Brian Huisman, from his blog entry entitled "CSV String to Array in JavaScript":
     * http://www.greywyvern.com/?post=258
     *
     */
    String.prototype.splitCSV = function(sep) {
        for (var thisCSV = this.split(sep = sep || ","), x = thisCSV.length - 1, tl; x >= 0; x--) {
            if (thisCSV[x].replace(/"\s+$/, '"').charAt(thisCSV[x].length - 1) == '"') {
                if ((tl = thisCSV[x].replace(/^\s+"/, '"')).length > 1 && tl.charAt(0) == '"') {
                    thisCSV[x] = thisCSV[x].replace(/^\s*"|"\s*$/g, '').replace(/""/g, '"');
                } else if (x) {
                    thisCSV.splice(x - 1, 2, [thisCSV[x - 1], thisCSV[x]].join(sep));
                } else thisCSV = thisCSV.shift().split(sep).concat(thisCSV);
            } else thisCSV[x].replace(/""/g, '"');
        } return thisCSV;
    };

    $.fn.CSVToTable = function(csvFile, options) {
        var defaults = {
            tableClass: "CSVTable",
            theadClass: "",
            thClass: "",
            tbodyClass: "",
            trClass: "",
            tdClass: "",
            loadingImage: "",
            loadingText: "Loading CSV data...",
            separator: ",",
            startLine: 0
        };
        var options = $.extend(defaults, options);
        return this.each(function() {
            var obj = $(this);
            var error = '';
            (options.loadingImage) ? loading = '<div style="text-align: center"><img alt="' + options.loadingText + '" src="' + options.loadingImage + '" /><br>' + options.loadingText + '</div>' : loading = options.loadingText;
            obj.html(loading);
            $.get(csvFile, function(data) {
                var tableHTML = '<table class="' + options.tableClass + '">';
                var lines = data.replace('\r','').split('\n');
                var printedLines = 0;
                var headerCount = 0;
                var headers = new Array();
                $.each(lines, function(lineCount, line) {
                    if ((lineCount == 0) && (typeof(options.headers) != 'undefined')) {
                        headers = options.headers;
                        headerCount = headers.length;
                        tableHTML += '<thead class="' + options.theadClass + '"><tr class="' + options.trClass + '">';
                        $.each(headers, function(headerCount, header) {
                            tableHTML += '<th class="' + options.thClass + '">' + header + '</th>';
                        });
                        tableHTML += '</tr></thead><tbody class="' + options.tbodyClass + '">';
                    }
                    if ((lineCount == options.startLine) && (typeof(options.headers) == 'undefined')) {
                        headers = line.splitCSV(options.separator);
                        headerCount = headers.length;
                        tableHTML += '<thead class="' + options.theadClass + '"><tr class="' + options.trClass + '">';
                        $.each(headers, function(headerCount, header) {
                            tableHTML += '<th class="' + options.thClass + '">' + header + '</th>';
                        });
                        tableHTML += '</tr></thead><tbody class="' + options.tbodyClass + '">';
                    } else if (lineCount >= options.startLine) {
                        var items = line.splitCSV(options.separator);
                        if (items.length > 1) {
                            printedLines++;
                            if (items.length != headerCount) {
                                error += 'error on line ' + lineCount + ': Item count (' + items.length + ') does not match header count (' + headerCount + ') \n';
                            }
                            (printedLines % 2) ? oddOrEven = 'odd' : oddOrEven = 'even';
                            tableHTML += '<tr class="' + options.trClass + ' ' + oddOrEven + '">';
                            $.each(items, function(itemCount, item) {
                                tableHTML += '<td class="' + options.tdClass + '">' + item + '</td>';
                            });
                            tableHTML += '</tr>';
                        }
                    }
                });
                tableHTML += '</tbody></table>';
                if (error) {
                    obj.html(error);
                } else {
                    obj.fadeOut(500, function() {
                        obj.html(tableHTML)
                    }).fadeIn(function() {
                        // trigger loadComplete
                        setTimeout(function() {
                            obj.trigger("loadComplete");
                        },0);
                    });
                }
            });
        });
    };

})(jQuery);