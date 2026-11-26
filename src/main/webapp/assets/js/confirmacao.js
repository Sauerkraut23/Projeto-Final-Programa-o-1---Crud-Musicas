document.addEventListener('DOMContentLoaded', function () {
    var modal = document.getElementById('modalExcluir');
    if (!modal) {
        return;
    }

    modal.addEventListener('show.bs.modal', function (evento) {
        var botao = evento.relatedTarget;
        if (!botao) {
            return;
        }
        modal.querySelector('form').setAttribute('action', botao.getAttribute('data-action'));
        modal.querySelector('input[name="id"]').value = botao.getAttribute('data-id');
        modal.querySelector('.nome-registro').textContent = botao.getAttribute('data-nome');
    });
});
