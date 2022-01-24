function Write_Text(){
    let x = document.forms["fdpessoais"]["photoWebsite"].value;
    if(x == "no"){
        document.forms["fdpessoais"]["caixaTexto"].disabled = true;
        document.forms["fdpessoais"]["caixaTexto"].value="";
    } else{
        document.forms["fdpessoais"]["caixaTexto"].disabled = false;
    }
}

function checkBrowser(elemento) {
    let opcao1 = document.getElementById("topPick");
    let opcao2 = document.getElementById("secondPick");
    let opcao3 = document.getElementById("thirdPick");

    if((elemento.id.localeCompare("topPick") !== 0) && (elemento.value === opcao1.value)){
        opcao1.value = "";
    }

    if((elemento.id.localeCompare("secondPick") !== 0) && (elemento.value === opcao2.value)){
        opcao2.value = "";
    }

    if((elemento.id.localeCompare("thirdPick") !== 0) && (elemento.value === opcao3.value)){
        opcao3.value = "";
    }
}

function getLastKey(){
    let itemKey;
    let values;
    let arrKeyValue = new Array();
    for(let i = 0; i < window.localStorage.length; i++){
        itemKey = localStorage.key(i);
        values = localStorage.getItem(itemKey);
        //alert(itemKey);
    }
    arrKeyValue[0] = itemKey;
    arrKeyValue[1] = values;
    return arrKeyValue;
}

function printLocal(){
    let aux = getLastKey();
    parser = new DOMParser();
    document.write("<p>" + "Q1-->" + aux[1].parser.parseFromString(text,"text/xml") +"</p>");
}


//isto tem que ficar fora da funcao para nao ser reescrito por cima quando vou para uma nova página
function validateForm2(){
    let d = new Date();
    //let xmlRowString = "<Questionario><2P>"
    let xmlRowString ="<Questionario>";

        let Rq = document.forms["fdpessoais"]["idade"].value;
        xmlRowString += '<q id="q1">' + Rq +'</q>';

        let Rq1 = document.forms["fdpessoais"]["sexo"].value;
        xmlRowString += '<q id="q2">' + Rq1 +'</q>';

        let Rq2 = document.forms["fdpessoais"]["netUse"].value;
        xmlRowString += '<q id="q3">' + Rq2 + '</q>';

        let Rq3 = document.forms["fdpessoais"]["q4"].value;
        xmlRowString += '<q id="q4">' + Rq3 + '</q>';

        let Rq32 = document.forms["fdpessoais"]["q42"].value;
        xmlRowString += '<q id="q42">' + Rq32 + '</q>';

        let Rq33 = document.forms["fdpessoais"]["q43"].value;
        xmlRowString += '<q id="q43">' + Rq33 + '</q>';

        let Rq4 = document.forms["fdpessoais"]["photoWebsite"].value;
        xmlRowString += '<q id="q5">' + Rq4 + '</q>';

        if(Rq4 == "yes"){
            let Rq41 = document.getElementById("caixaTexto5").value;
            xmlRowString += '<q id="q51">' + Rq41 + '</q>';
        }

        //xmlRowString += "</2P>";
        //alert(xmlRowString);
        window.localStorage.setItem(d.getTime(), xmlRowString);
}

function validateForm3(){

    //let xmlRowString = "<3P>"
    let xmlRowString = ""

    let Rq5 = document.forms["tarefas1"]["caixaTexto7"].value;
        xmlRowString += '<q id="q6">' + Rq5 + '</q>';

        let Rq6 = document.forms["tarefas1"]["range"].value;
        xmlRowString += '<q id="q7">' + Rq6 + '</q>';
        
        let Rq7 = document.getElementById("texto9").value;
        //alert(Rq7);
        xmlRowString += '<q id="q8">' + Rq7 + '</q>';

        /*
        let Rq7 = document.forms["tarefas1"]["caixaTexto9"].value;
        xmlRowString += '<q id="q8">' + Rq7 + '</q>';
        */
       
       //xmlRowString += "</3P>";
       //alert(xmlRowString);
       // este array aux contem a key na posicao 0, e o valor na posicao 1
       let aux;
       aux = getLastKey();
       window.localStorage.setItem( aux[0], aux[1] + xmlRowString);
}


function validateForm4(){
    //let xmlRowString = "<4P>"
    let xmlRowString = "";

    let Rq8 = document.forms["tarefas2"]["range"].value;
        xmlRowString += '<q id="q9">' + Rq8 +'</q>';

        let Rq9 = document.getElementById("texto11").value;
        xmlRowString += '<q id="q10">' + Rq9 +'</q>';

        let Rq10 = document.forms["tarefas2"]["range2"].value;
        xmlRowString += '<q id="q11">' + Rq10 + '</q>';

        let Rq11 = document.forms["tarefas2"]["range3"].value;
        xmlRowString += '<q id="q12">' + Rq11 + '</q>';

        let Rq12 = document.getElementById("texto14").value;
        xmlRowString += '<q id="q13">' + Rq12 + '</q>';

        let Rq13 = document.getElementById("texto15").value;
        xmlRowString += '<q id="q14">' + Rq13 + '</q>';

        //xmlRowString += "</4P>";
        //alert(xmlRowString);
        let aux;
        aux = getLastKey();
        window.localStorage.setItem( aux[0], aux[1] + xmlRowString);
}

function validateForm5(){

        //let xmlRowString = "<5P>"
        let xmlRowString="";

        let Rq14 = document.forms["tarefas3"]["range11"].value;
        xmlRowString += '<q id="q15">' + Rq14 +'</q>';

        let Rq114 = document.forms["tarefas3"]["range12"].value;
        xmlRowString += '<q id="q115">' + Rq114 +'</q>';
        
        let Rq124 = document.forms["tarefas3"]["range13"].value;
        xmlRowString += '<q id="q125">' + Rq124 +'</q>';

        let Rq134 = document.forms["tarefas3"]["range14"].value;
        xmlRowString += '<q id="q135">' + Rq134 +'</q>';



        let Rq15 = document.forms["tarefas3"]["range2"].value;
        xmlRowString += '<q id="q16">' + Rq15 +'</q>';

        let Rq16 = document.forms["tarefas3"]["range3"].value;
        xmlRowString += '<q id="q17">' + Rq16 + '</q>';

        let Rq17 = document.forms["tarefas3"]["range4"].value;
        xmlRowString += '<q id="q18">' + Rq17 + '</q>';

        let Rq18 = document.forms["tarefas3"]["range5"].value;
        xmlRowString += '<q id="q19">' + Rq18 + '</q>';
        
        //xmlRowString += "</5P>";
        //alert(xmlRowString);
        let aux;
        aux = getLastKey();
        window.localStorage.setItem( aux[0], aux[1] + xmlRowString);

}

function validateForm6(){
    //let xmlRowString = "<6P>"
    let xmlRowString = "";


    let Rq19 = document.forms["tarefas4"]["range2"].value;
        xmlRowString += '<q id="q20">' + Rq19 +'</q>';

        let Rq20 = document.forms["tarefas4"]["range3"].value;
        xmlRowString += '<q id="q21">' + Rq20 +'</q>';

        let Rq21 = document.forms["tarefas4"]["range4"].value;
        xmlRowString += '<q id="q22">' + Rq21 + '</q>';

        let Rq22 = document.forms["tarefas4"]["range5"].value;
        xmlRowString += '<q id="q23">' + Rq22 + '</q>';

        let Rq23 = document.forms["tarefas4"]["range6"].value;
        xmlRowString += '<q id="q24">' + Rq23 + '</q>';

        let Rq24 = document.forms["tarefas4"]["range7"].value;
        xmlRowString += '<q id="q25">' + Rq24 + '</q>';

        xmlRowString += "</Questionario>";

        //alert(xmlRowString);
        //so vou gravar os dados todos, quando terminar a ultima pagina
        let aux;
        aux = getLastKey();
        window.localStorage.setItem(aux[0], aux[1] + xmlRowString);
        alert(xmlRowString);

}
/*

    if(flag == 2 ){
        let Rq5 = document.forms["tarefas1"]["caixaTexto7"].value;
        xmlRowString += '<q id="q6">' + Rq5 + '</q>';

        let Rq6 = document.forms["tarefas1"]["range"].value;
        xmlRowString += '<q id="q7">' + Rq6 + '</q>';
        
        let Rq7 = document.getElementById("texto9").value;
        //alert(Rq7);
        xmlRowString += '<q id="q8">' + Rq7 + '</q>';

        /*
        let Rq7 = document.forms["tarefas1"]["caixaTexto9"].value;
        xmlRowString += '<q id="q8">' + Rq7 + '</q>';
        */
       /*
       xmlRowString += "<2P>";
       alert(xmlRowString);
       window.localStorage.setItem(d.getTime(), xmlRowString);

    }

    if(flag == 3){
        let Rq8 = document.forms["tarefas2"]["range"].value;
        xmlRowString += '<q id="q9">' + Rq8 +'</q>';

        let Rq9 = document.getElementById("texto11").value;
        xmlRowString += '<q id="q10">' + Rq9 +'</q>';

        let Rq10 = document.forms["tarefas2"]["range2"].value;
        xmlRowString += '<q id="q11">' + Rq10 + '</q';

        let Rq11 = document.forms["tarefas2"]["range3"].value;
        xmlRowString += '<q id="q12">' + Rq11 + '</q>';

        let Rq12 = document.getElementById("texto14").value;
        xmlRowString += '<q id="q13">' + Rq12 + '</q>';

        let Rq13 = document.getElementById("texto15").value;
        xmlRowString += '<q id="q14">' + Rq13 + '</q>';

        xmlRowString += "<3P>";
        alert(xmlRowString);
        window.localStorage.setItem(d.getTime(), xmlRowString);
    }

    if(flag == 4){

        let Rq14 = document.forms["tarefas3"]["range11"].value;
        xmlRowString += '<q id="q15">' + Rq14 +'</q>';

        let Rq114 = document.forms["tarefas3"]["range12"].value;
        xmlRowString += '<q id="q15">' + Rq114 +'</q>';
        
        let Rq124 = document.forms["tarefas3"]["range13"].value;
        xmlRowString += '<q id="q15">' + Rq124 +'</q>';

        let Rq134 = document.forms["tarefas3"]["range14"].value;
        xmlRowString += '<q id="q15">' + Rq134 +'</q>';



        let Rq15 = document.forms["tarefas3"]["range2"].value;
        xmlRowString += '<q id="q16">' + Rq15 +'</q>';

        let Rq16 = document.forms["tarefas3"]["range3"].value;
        xmlRowString += '<q id="q17">' + Rq16 + '</q';

        let Rq17 = document.forms["tarefas3"]["range4"].value;
        xmlRowString += '<q id="q18">' + Rq17 + '</q>';

        let Rq18 = document.forms["tarefas3"]["range5"].value;
        xmlRowString += '<q id="q19">' + Rq18 + '</q>';
        
        xmlRowString += "<4P>";
        alert(xmlRowString);
        window.localStorage.setItem(d.getTime(), xmlRowString);
    }

    if(flag == 5){

        let Rq19 = document.forms["tarefas4"]["range2"].value;
        xmlRowString += '<q id="q20">' + Rq19 +'</q>';

        let Rq20 = document.forms["tarefas4"]["range3"].value;
        xmlRowString += '<q id="q21">' + Rq20 +'</q>';

        let Rq21 = document.forms["tarefas4"]["range4"].value;
        xmlRowString += '<q id="q22">' + Rq21 + '</q';

        let Rq22 = document.forms["tarefas4"]["range5"].value;
        xmlRowString += '<q id="q23">' + Rq22 + '</q>';

        let Rq23 = document.forms["tarefas4"]["range6"].value;
        xmlRowString += '<q id="q24">' + Rq23 + '</q>';

        let Rq24 = document.forms["tarefas4"]["range7"].value;
        xmlRowString += '<q id="q25">' + Rq24 + '</q>';

        xmlRowString += "</Questionario>";

        alert(xmlRowString);
        //so vou gravar os dados todos, quando terminar a ultima pagina
        window.localStorage.setItem(d.getTime(), xmlRowString);
        alert(xmlRowString);
    }

    /*
    xmlRowString += "<Questionario>";
    window.localStorage.setItem(d.getTime(), xmlRowString);
    */
   /*
}
*/

/*
function getdataForm(){
    let todo_index = window.localStorage.length;
    let xmlDoc;
    for(let k = todo_index; k > 0; k--){    //este array percorre todas as linhas, tem que ser a decrementar senão a ordem dos utilizadores ficam invertida
        document.write("<h1> Respostas dadas no Questionário: Utilizador" +k+ "</h1>");
        for(let i = 0; i < todo_index; i++){        //este array percorre a lina do localStorage
            let localStorageRow = window.localStorage.getItem(window.localStorage.key(i));
            if(window.DOMParser){
                let parser = new DOMParser();
                xmlDoc = parser.parseFromString(localStorageRow, "text/xml");
                //let x = xmlDoc.getElementsByTagName("q");
                //document.write("<p>Idade" + x[0].childNodes[0].nodeValue +  "</p>");
                //document.write("<p>Sexo" + x[1].childNodes[0].nodeValue +  "</p>");
            }
        }

        let x = xmlDoc.getElementsByTagName("q");
        //console.log(x);

        for(let v = 0; v < x.length; v++){  //depois de ter a primeira linha guardada em x, vou imprimi-la toda  percorer
                                        //todos os nós como o  prof tem no ppt
                document.write("<p>"+ x[v].id+ ": " + x[v].childNodes[0].nodeValue + "</p>");
        }
    }
}
*/

function getdataForm() {
    let todo_index = window.localStorage.length;
    let xmlDoc;

    for(let k = 0; k < window.localStorage.length; k++){
        document.write("<h3>" + "Respostas dadas pelo Utilizador"+ k + ": "+ "</h3>");

        for (let i = 0; i < todo_index; i++) {
            let localStorageRow = window.localStorage.getItem(window.localStorage.key(k));
            if (window.DOMParser) {
                let parser = new DOMParser();
                xmlDoc=parser.parseFromString(localStorageRow,"text/xml");
                //console.log(i);
            }
        }
        let x = xmlDoc.getElementsByTagName("q");

        for(let k = 0; k < x.length; k++){
                document.write("<p>"+ x[k].id+ ": " + x[k].childNodes[0].nodeValue + "</p>");
        }

    }

}
