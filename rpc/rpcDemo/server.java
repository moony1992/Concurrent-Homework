@Service
public class RpcServer implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @PostConstruct
  public void init() {
    new Thread(this::initConn).start();
  }

  public void initConn() {
    System.out.println("init socket...");
    try (ServerSocket ss = new ServerSocket(8888)) {
      while (true) {
        Socket socket = ss.accept();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        RpcRequest request = (RpcRequest) ois.readObject();

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(process(request));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Object process(RpcRequest request) throws Exception {
    Object obj = applicationContext.getBean(request.getClassName());
    Method method;
    if (request.getArgs() != null) {
      Class<?>[] cls = new Class[request.getArgs().length];
      for (int i = 0; i < request.getArgs().length; i++) {
        cls[i] = request.getArgs()[i].getClass();
      }

      method = obj.getClass().getMethod(request.getMethodName(), cls);
    } else {
      method = obj.getClass().getMethod(request.getMethodName());
    }

    return method.invoke(obj, request.getArgs());
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
