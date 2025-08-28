package co.com.pragma.autenticacion.api.docs;

public final class UserApiDocs {

    private UserApiDocs(){}

    public static final String TAG = "Usuarios";

    public static final String REG_SUMMARY = "Registrar usuario";
    public static final String REG_DESC = "Crea un usuario aplicando validaciones e invariantes de negocio.";

    public static final String LIST_SUMMARY = "Listar usuarios";
    public static final String LIST_DESC = "Retorna el listado completo de usuarios.";

    public static final String GET_BY_DOC_SUMMARY = "Obtener usuario por documento";
    public static final String GET_BY_DOC_DESC = "Busca un usuario por su número de documento de identidad.";
}