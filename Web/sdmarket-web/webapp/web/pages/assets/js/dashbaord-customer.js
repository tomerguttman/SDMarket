var RECHARGE_BALANCE_URL = buildUrlWithContextPath("recharge-balance");

let click = $("#buttonRecharge").click(() => {
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
                        addTransactionToTransactionsOverview(datePicked, amountToRecharge, data.balanceBefore, data.balanceAfter);
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

function updateCurrentBalanceSpan(balanceAfter) {
    document.querySelector('#currentBalanceSpan').innerHTML = "$" + balanceAfter;
    console.log("Balance was recharged with:" + balanceAfter);
}

function addTransactionToTransactionsOverview(datePicked, amountToRecharge, balanceBefore, balanceAfter) {
    var newTransactionRow = $("<tr>\n" +
                "<td>" + "Recharge" + "</td>\n" +
                "<td>" + datePicked + "</td>\n" +
                "<td>" + "$" + amountToRecharge + "</td>\n" +
                "<td>" + "$" + balanceBefore + "</td>\n" +
                "<td>" +  "$" + balanceAfter + "</td>" +
                "</tr>\n");
    $('#transactionsOverviewTable tbody').append(newTransactionRow);
}