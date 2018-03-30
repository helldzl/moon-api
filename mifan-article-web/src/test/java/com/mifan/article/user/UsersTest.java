package com.mifan.article.user;

import com.mifan.article.AbstractTests;

// import org.springframework.validation.Validator;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/16
 */
public class UsersTest extends AbstractTests {

//    @Autowired
//    private Validator validator;
//
//    private String mobile = AccountType.REGEXP_MOBILE;
//
//    @Test
//    public void testA() {
//        Users user = new Users();
//        Set<ConstraintViolation<Users>> set = validator.validate(user);
//        it(set);
//    }
//
//    private <T> void it(Set<ConstraintViolation<T>> set) {
//        set.iterator().forEachRemaining(item -> {
//            System.out.println(item);
//            System.out.println(item.getMessage());
//        });
//    }
//
//    @Test
//    public void testAuth() {
//        Authorities a = new Authorities();
//        AuthOperations o = new AuthOperations();
//        o.setFunctionClass("com...#");
//        a.setAuthOperation(o);
//
//        Set<ConstraintViolation<Authorities>> set = validator.validate(a, ValidationGroups.Put.class);
//        it(set);
//    }
//
//    @Test
//    public void testUsers() {
//
//
//        Users user = new Users();
//        user.setUsername("1");
//        UserProfiles userProfiles = new UserProfiles();
//        userProfiles.setGender(10);
//        user.setUserProfile(userProfiles);
//        Set<ConstraintViolation<Users>> constraintViolations = validator.validate(user);
//
//        // Set<ConstraintViolation<UserProfiles>> constraintViolations2 = validator.validate(userProfiles);
//        System.out.println(constraintViolations);
//
//        constraintViolations = validator.validate(user, ValidationGroups.Post.class);
//        System.out.println(constraintViolations);
//
//        constraintViolations = validator.validateProperty(user, "username", ValidationGroups.Post.class);
//        System.out.println(constraintViolations);
//        constraintViolations.iterator().forEachRemaining(usersConstraintViolation -> {
//            String s1 = usersConstraintViolation.getMessage();
//            String s2 = usersConstraintViolation.getPropertyPath().toString();
//            String s3 = usersConstraintViolation.getRootBean().toString();
//            String s4 = usersConstraintViolation.getRootBeanClass().getSimpleName();
//            System.out.println();
//        });
//    }
//
//    @Test
//    public void testBinder() {
//        MutablePropertyValues values = new MutablePropertyValues();
//        values.add("username", "1");
//
//        Users target = new Users();
//        // target.setUsername("1");
//        DataBinder binder = new DataBinder(target);
//        binder.bind(values);
//        // binder.setValidator(validator);
//
//        // bind to the target object
//        // binder.bi
//
//        // validate the target object
//        binder.validate();
//
//        // get BindingResult that includes any validation errors
//        BindingResult results = binder.getBindingResult();
//        for (FieldError fieldError : results.getFieldErrors()) {
//            System.out.println(fieldError.getDefaultMessage());
//        }
//        System.out.println();
//    }

}
