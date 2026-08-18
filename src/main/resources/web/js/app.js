function testPing() {
    // Verificamos que el puente exista antes de llamarlo
    if (window.javaBridge) {
        // Llamamos al método ping() de Java
        const respuesta = window.javaBridge.ping();

        // Imprimimos la respuesta en el HTML y en la consola
        document.getElementById('resultado').innerText = "Java dice: " + respuesta;
        console.log("Ping exitoso:", respuesta);
    } else {
        document.getElementById('resultado').innerText = "Error: javaBridge no está definido.";
        document.getElementById('resultado').style.color = "red";
    }
}