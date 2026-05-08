package repository;

public class RepositoryManager {

    private static RepositoryManager repositoryManager;
    private static UserRepository userRepository;
    // private static BookRepository bookRepository;
    // private static ReaderRepository readerRepository;
    // private static CallSlipRepository callSlipRepository;
    // private static ReturnSlipRepository returnSlipRepository;

    private RepositoryManager() {
        userRepository = UserRepository.getInstance();
        // bookRepository = BookRepository.getInstance();
        // readerRepository = ReaderRepository.getInstance();
        // callSlipRepository = CallSlipRepository.getInstance();
        // returnSlipRepository = ReturnSlipRepository.getInstance();

        userRepository.importData("resource/users.txt");
        // bookRepository.importData("resource/books.txt");
        // readerRepository.importData("resource/readers.txt");
        // GenreRepository.importData("resource/genres.txt");
        // callSlipRepository.importData("resource/call_slips.txt");
        // returnSlipRepository.importData("resource/return_slips.txt");
    }

    public static RepositoryManager getInstance() {
        if (repositoryManager == null) {
            return new RepositoryManager();
        }

        return repositoryManager;
    }

    public static void store() {
        // userRepository.exportData("resource/librarians.txt");
        // bookRepository.exportData("resource/books.txt");
        // readerRepository.exportData("resource/readers.txt");
        // GenreRepository.exportData("resource/genres.txt");
        // callSlipRepository.exportData("resource/call_slips.txt");
        // returnSlipRepository.exportData("resource/return_slips.txt");
    }
}
