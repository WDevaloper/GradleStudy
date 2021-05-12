package com.github.gradle;

import androidx.fragment.app.Fragment;

import com.github.router.annotate.Parameter;

import java.util.List;

public class TestFragment2 extends Fragment {

    @Parameter
    String param;

    @Parameter
    int param2;

    @Parameter
    long long_param;

    @Parameter
    Long long_param2;


    @Parameter
    int[] int_param;


    @Parameter
    String[] string_param;


    @Parameter
    List<User>[] list_param;


    @Parameter
    User user;

    @Parameter(name = "person")
    Person person;

}
