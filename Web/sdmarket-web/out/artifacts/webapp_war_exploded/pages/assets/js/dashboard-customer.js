var RECHARGE_BALANCE_URL = buildUrlWithContextPath("recharge-balance");
var REFRESH_DASHBOARD_URL = buildUrlWithContextPath("load-dashboard");
$(document).ready(function(){
    refreshDashboardInformation();
    setInterval(refreshDashboardInformation, 2000);
    $("#buttonRecharge").click(() => {
        try {
            const datePicked = document.querySelector('#rechargeDatePicker').value;

            if(!(datePicked === "")) {
                const amountToRecharge = parseFloat(document.querySelector("#amountMoneyToRechargeInput").value);
                if(isNaN(amountToRecharge)) { alert("Input is not a number"); }
                else {
                    console.log("amountToRecharge:" + amountToRecharge);
                    $.ajax({
                        type: 'POST',
                        url: RECHARGE_BALANCE_URL,
                        data: {
                            "amountToRecharge": amountToRecharge,
                            "dateOfRecharge": datePicked
                        },
                        success:function(data){
                            //'data' is the value returned.
                            updateCurrentBalanceSpan(data.balanceAfter);
                            addTransactionToTransactionsOverview("Recharge", datePicked, amountToRecharge, data.balanceBefore, data.balanceAfter);
                            $('#rechargeDatePicker').val("");
                            $("#amountMoneyToRechargeInput").val("");

                        },
                        error:function(){
                            alert('An error was encountered.');
                        }
                    });
                }
            }
            else { alert("Please choose a date first"); }
        }
        catch (e) {
            console.log(e.message);
        }
    });
})

function updateCurrentBalanceSpan(balanceAfter) {
    document.querySelector('#currentBalanceSpan').innerHTML = "$" + balanceAfter;
    console.log("Balance was recharged with:" + balanceAfter);
}

function addTransactionToTransactionsOverview(transactionType, datePicked, amountToRecharge, balanceBefore, balanceAfter) {
    var newTransactionRow = $("<tr>\n" +
                "<td>" + transactionType + "</td>\n" +
                "<td>" + datePicked + "</td>\n" +
                "<td>" + "$" + amountToRecharge + "</td>\n" +
                "<td>" + "$" + balanceBefore.toFixed(2) + "</td>\n" +
                "<td>" +  "$" + balanceAfter.toFixed(2) + "</td>" +
                "</tr>\n");
    $('#transactionsOverviewTable tbody').prepend(newTransactionRow);
}

function refreshDashboardInformation() {
    $.ajax({
        type: 'GET',
        contentType: "application/json",
        url: REFRESH_DASHBOARD_URL,
        success:function(data){
            //'data' is the value returned.
            loadNewDataToDashboard(data);
        },
        error:function(e){
            alert('An error was encountered.');
            console.log(e.responseText);
        }
    });
}

function updateDashboardLabels(currentBalance, totalOrders, averageOrdersCost, mostLovedItem) {
    $("#currentBalanceSpan").html("$" + currentBalance.toFixed(2));
    $("#totalOrdersSpan").html(totalOrders);
    $("#averageOrdersCostSpan").html("$" + averageOrdersCost.toFixed(2));
    $("#mostLovedItemSpan").html(mostLovedItem);
}

function updateDashboardZonesTable(systemZones) {
    $("#zonesOverviewTable tbody").empty();
    if(systemZones != null) {
        for (var zone of systemZones) {
            $('#zonesOverviewTable tbody').append(createZonesTableRow(zone));
        }
    }
}

function updateDashboardTransactionsTable(userTransactions) {
    $("#transactionsOverviewTable tbody").empty();
    userTransactions.forEach((transaction) => addTransactionToTransactionsOverview(transaction.type, transaction.date, transaction.amount, transaction.balanceBefore, transaction.balanceAfter));
}

function updateDashboardActiveUsersTable(otherUsers) {
    $("#activeUsersTable tbody").empty();
    for (var user of otherUsers){
        $("#activeUsersTable tbody").append(createActiveUserTableRow(user));
    }
}

function loadNewDataToDashboard(data) {
    updateDashboardLabels(data.currentBalance, data.totalOrders, data.averageOrdersCost, data.mostLovedItem);
    updateDashboardZonesTable(data.systemZones);
    updateDashboardTransactionsTable(data.userTransactions);
    updateDashboardActiveUsersTable(data.otherUsers);
    $('#usernameTopRightSpan').html(data.userName);
}

function createZoneButton(zoneName) {
    return '<button class="btn btn-primary btn-sm" type="submit" name="zoneName" value=' + zoneName.replace(/ /g, "_") + '>' +
        "Go To Zone" +
    "</button>";
}

function createZonesTableRow(zone) {
    return $("<tr>\n" +
        "<td>" + zone.zoneName + "</td>\n" +
        "<td>" + zone.ownerName + "</td>\n" +
        "<td>" + zone.amountOfItemTypesInZone + "</td>\n" +
        "<td>" + zone.amountOfStoresInZone + "</td>\n" +
        "<td>" + zone.amountOfOrdersInZone + "</td>" +
        "<td>" + zone.averageOrdersCostWithoutDelivery.toFixed(2) + "</td>" +
        "<td>" +
            "<form method='GET' action='/pages/order'>\n" +
                createZoneButton(zone.zoneName) +
            "</form>\n" +
        "</td>"+
        "</tr>\n");
}

function createActiveUserTableRow(user) {
    var realUserType;
    realUserType = user.userType === "customer" ? "Customer" : "Shop Owner";

    return $("<tr>\n" +
        "<td>" + user.name + "</td>\n" +
        "<td>" + realUserType + "</td>\n" +
        "</tr>\n");
}

